package com.hex.bigdata.udsp.im.converter.impl.wrapper;


import com.hex.bigdata.udsp.im.converter.BatchSourceConverter;
import com.hex.bigdata.udsp.im.converter.BatchTargetConverter;
import com.hex.bigdata.udsp.im.converter.RealtimeTargetConverter;
import com.hex.bigdata.udsp.im.converter.impl.model.datasource.KuduDatasource;
import com.hex.bigdata.udsp.im.converter.impl.model.metadata.KuduMetadata;
import com.hex.bigdata.udsp.im.converter.impl.util.KuduUtil;
import com.hex.bigdata.udsp.im.converter.impl.util.model.TableColumn;
import com.hex.bigdata.udsp.im.converter.impl.util.model.TblProperty;
import com.hex.bigdata.udsp.im.converter.impl.util.model.ValueColumn;
import com.hex.bigdata.udsp.im.converter.impl.util.model.WhereProperty;
import com.hex.bigdata.udsp.im.converter.model.Metadata;
import com.hex.bigdata.udsp.im.converter.model.MetadataCol;
import com.hex.bigdata.udsp.im.converter.model.ModelMapping;
import org.apache.kudu.ColumnSchema;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by PC on 2018/2/26.
 */
public abstract class KuduWrapper extends Wrapper implements BatchSourceConverter, BatchTargetConverter, RealtimeTargetConverter {
    private static Logger logger = LogManager.getLogger(KuduWrapper.class);

    protected static final String HIVE_ENGINE_STORAGE_HANDLER_CLASS = "com.hex.hive.kudu.KuduStorageHandler";

    /**
     * 增量插入
     * <p>
     * 注：没有相同主键时数据插入，有相同主键时数据不更新
     *
     * @param metadata
     * @param modelMappings
     * @param valueColumns
     * @throws Exception
     */
    @Override
    protected void insertInto(Metadata metadata, List<ModelMapping> modelMappings, List<ValueColumn> valueColumns) throws Exception {
        checkModelMappings(modelMappings);
        KuduDatasource datasource = new KuduDatasource(metadata.getDatasource());
        String tableName = metadata.getTbName();
        KuduUtil.insert(datasource, tableName, valueColumns);
    }

    /**
     * 更新、插入
     * <p>
     * 注：没有相同主键时数据插入，有相同主键时数据更新
     *
     * @param metadata
     * @param modelMappings
     * @param valueColumns
     * @param whereProperties
     * @throws Exception
     */
    @Override
    protected void updateInsert(Metadata metadata, List<ModelMapping> modelMappings, List<ValueColumn> valueColumns, List<WhereProperty> whereProperties) throws Exception {
        checkModelMappings(modelMappings);
        KuduDatasource datasource = new KuduDatasource(metadata.getDatasource());
        String tableName = metadata.getTbName();
        KuduUtil.upsert(datasource, tableName, valueColumns);
    }

    /**
     * 匹配更新
     * <p>
     * 注：没有相同主键时数据不插入，有相同主键时数据更新
     *
     * @param metadata
     * @param modelMappings
     * @param valueColumns
     * @param whereProperties
     * @throws Exception
     */
    @Override
    protected void matchingUpdate(Metadata metadata, List<ModelMapping> modelMappings, List<ValueColumn> valueColumns, List<WhereProperty> whereProperties) throws Exception {
        checkModelMappings(modelMappings);
        KuduDatasource datasource = new KuduDatasource(metadata.getDatasource());
        String tableName = metadata.getTbName();
        KuduUtil.update(datasource, tableName, valueColumns);
    }

    private void checkModelMappings(List<ModelMapping> modelMappings) {
        boolean flg = false;
        for (ModelMapping modelMapping : modelMappings) {
            if (modelMapping.getMetadataCol().isPrimary()) {
                flg = true;
                break;
            }
        }
        if (!flg) {
            throw new IllegalArgumentException("【映射字段】的目标字段必须要有一个是主键字段！");
        }
    }

    @Override
    protected List<String> getSelectColumns(List<ModelMapping> modelMappings, Metadata metadata) {
        if (modelMappings == null || modelMappings.size() == 0)
            return null;
        List<String> selectColumns = new ArrayList<>();
        for (ModelMapping mapping : modelMappings)
            selectColumns.add(mapping.getName());
        return selectColumns;
    }

    @Override
    protected void emptyDatas(Metadata metadata) throws Exception {
        KuduMetadata kuduMetadata = new KuduMetadata(metadata);
        KuduUtil.emptyHTable(kuduMetadata);
    }

    protected List<MetadataCol> getColumns(KuduDatasource datasource, String tableName) {
        List<ColumnSchema> columns = KuduUtil.getColumns(datasource, tableName);
        if (columns == null) return null;
        List<MetadataCol> mdCols = new ArrayList<>();
        MetadataCol mdCol = null;
        ColumnSchema column = null;
        for (int i = 0; i < columns.size(); i++) {
            column = columns.get(i);
            mdCol = new MetadataCol();
            mdCol.setSeq((short) (i + 1));
            mdCol.setName(column.getName());
            mdCol.setDescribe(mdCol.getName());
            mdCol.setType(KuduUtil.getColType(column.getType()));
            mdCol.setPrimary(column.isKey());
            mdCol.setIndexed(false);
            mdCol.setStored(true);
            mdCols.add(mdCol);
        }
        return mdCols;
    }

    protected List<TableColumn> getTargetColumns(List<ModelMapping> modelMappings) {
        List<TableColumn> columns = new ArrayList<>();
        for (ModelMapping mapping : modelMappings) {
            MetadataCol metadataCol = mapping.getMetadataCol();
            String dataType = getDataType(metadataCol.getType(), metadataCol.getLength());
            columns.add(new TableColumn(metadataCol.getName(), dataType, metadataCol.getDescribe()));
        }
        return columns;
    }

    protected List<TableColumn> getSourceColumns(List<ModelMapping> modelMappings) {
        List<TableColumn> columns = new ArrayList<>();
        for (ModelMapping mapping : modelMappings) {
            String dataType = getDataType(mapping.getType(), mapping.getLength());
            columns.add(new TableColumn(mapping.getName(), dataType, mapping.getDescribe()));
        }
        return columns;
    }

    protected List<TblProperty> getSourceTblProperties(KuduDatasource datasource, String tableName) {
        List<TblProperty> tblProperties = new ArrayList<>();
        tblProperties.add(new TblProperty("kudu.master.addresses", datasource.getKuduMasterHosts())); // Kudu服务器地址
        tblProperties.add(new TblProperty("kudu.table.name", tableName)); // Kudu表名
        return tblProperties;
    }

    protected List<TblProperty> getTargetTblProperties(KuduDatasource datasource, String tableName) {
        List<TblProperty> tblProperties = new ArrayList<>();
        tblProperties.add(new TblProperty("kudu.master.addresses", datasource.getKuduMasterHosts())); // Kudu服务器地址
        tblProperties.add(new TblProperty("kudu.table.name", tableName)); // Kudu表名
        return tblProperties;
    }
}
