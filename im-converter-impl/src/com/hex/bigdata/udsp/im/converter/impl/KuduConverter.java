package com.hex.bigdata.udsp.im.converter.impl;

import com.hex.bigdata.udsp.common.api.model.Datasource;
import com.hex.bigdata.udsp.im.converter.impl.model.datasource.HiveDatasource;
import com.hex.bigdata.udsp.im.converter.impl.model.datasource.KuduDatasource;
import com.hex.bigdata.udsp.im.converter.impl.model.metadata.KuduMetadata;
import com.hex.bigdata.udsp.im.converter.impl.model.modeling.KuduModel;
import com.hex.bigdata.udsp.im.converter.impl.util.HiveSqlUtil;
import com.hex.bigdata.udsp.im.converter.impl.util.JdbcUtil;
import com.hex.bigdata.udsp.im.converter.impl.util.KuduUtil;
import com.hex.bigdata.udsp.im.converter.impl.util.model.TableColumn;
import com.hex.bigdata.udsp.im.converter.impl.util.model.TblProperty;
import com.hex.bigdata.udsp.im.converter.impl.wrapper.KuduWrapper;
import com.hex.bigdata.udsp.im.converter.model.Metadata;
import com.hex.bigdata.udsp.im.converter.model.MetadataCol;
import com.hex.bigdata.udsp.im.converter.model.Model;
import com.hex.bigdata.udsp.im.converter.model.ModelMapping;
import org.apache.kudu.client.KuduClient;

import java.util.List;

/**
 * Created by PC on 2018/2/26.
 */
//@Component("com.hex.bigdata.udsp.im.converter.impl.KuduConverter")
public class KuduConverter extends KuduWrapper {

    @Override
    public boolean testDatasource(Datasource datasource) {
        KuduDatasource kuduDatasource = new KuduDatasource(datasource);
        KuduClient client = null;
        try {
            client = KuduUtil.getClient(kuduDatasource);
            if (client != null) return true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            KuduUtil.release(kuduDatasource, client);
        }
        return false;
    }

    @Override
    public List<MetadataCol> columnInfo(Metadata metadata) {
        KuduDatasource datasource = new KuduDatasource(metadata.getDatasource());
        String tableName = metadata.getTbName();
        return getColumns(datasource, tableName);
    }

    @Override
    public void createSchema(Metadata metadata) throws Exception {
        KuduMetadata kuduMetadata = new KuduMetadata(metadata);
        KuduUtil.createTable(kuduMetadata, true);
    }

    @Override
    public void dropSchema(Metadata metadata) throws Exception {
        KuduDatasource datasource = new KuduDatasource(metadata.getDatasource());
        String tableName = metadata.getTbName();
        KuduUtil.dropTable(datasource, tableName, true);
    }

    @Override
    public boolean checkSchema(Metadata metadata) throws Exception {
        KuduDatasource datasource = new KuduDatasource(metadata.getDatasource());
        String tableName = metadata.getTbName();
        return KuduUtil.tableExists(datasource, tableName);
    }

    @Override
    public void addColumns(Metadata metadata, List<MetadataCol> addMetadataCols) throws Exception {
        if (addMetadataCols != null && addMetadataCols.size() != 0) {
            KuduMetadata kuduMetadata = new KuduMetadata(metadata);
            KuduUtil.addColumns(kuduMetadata, addMetadataCols);
        }
    }

    @Override
    public void createTargetEngineSchema(Model model) throws Exception {
        Metadata metadata = model.getTargetMetadata();
        HiveDatasource eHiveDs = new HiveDatasource(model.getEngineDatasource());
        KuduMetadata kuduMetadata = new KuduMetadata(metadata);
        KuduDatasource kuduDs = new KuduDatasource(metadata.getDatasource());
        List<ModelMapping> modelMappings = model.getModelMappings();
        String tableName = getTargetTableName(model.getId());
        String kuduTableName = kuduMetadata.getTbName();
        List<TableColumn> tableColumns = getTargetColumns(modelMappings);
        List<TblProperty> tblProperties = getTargetTblProperties(kuduDs, kuduTableName);
        String sql = HiveSqlUtil.createStorageHandlerTable(true, true, tableName,
                tableColumns, "目标的Hive引擎表", null,
                HIVE_ENGINE_STORAGE_HANDLER_CLASS, null, tblProperties);
        JdbcUtil.createEngineSchema(eHiveDs, HIVE_ENGINE_DATABASE_NAME, sql);
    }

    /**
     * 创建源引擎Schema
     *
     * @param model
     * @throws Exception
     */
    @Override
    public void createSourceEngineSchema(Model model) throws Exception {
        HiveDatasource eHiveDs = new HiveDatasource(model.getEngineDatasource());
        String id = model.getId();
        KuduModel kuduModel = new KuduModel(model);
        String kuduTableName = kuduModel.getKuduTableName();
        String engineSchemaName = getSourceTableName(id);
        KuduDatasource kuduDs = new KuduDatasource(model.getSourceDatasource());
        List<ModelMapping> modelMappings = model.getModelMappings();
        List<TableColumn> tableColumns = getSourceColumns(modelMappings);
        List<TblProperty> tblProperties = getSourceTblProperties(kuduDs, kuduTableName);
        String sql = HiveSqlUtil.createStorageHandlerTable(true, true, engineSchemaName,
                tableColumns, "源的Hive引擎表", null,
                HIVE_ENGINE_STORAGE_HANDLER_CLASS, null, tblProperties);
        JdbcUtil.createEngineSchema(eHiveDs, HIVE_ENGINE_DATABASE_NAME, sql);
    }

    /**
     * 创建源引擎Schema（只针对非暴力查询模式时使用）
     *
     * @param model
     * @param engineSchemaName
     * @throws Exception
     */
    @Override
    public void createSourceEngineSchema(Model model, String engineSchemaName) throws Exception {
        throw new RuntimeException("Kudu表作为源时不支持非暴力查询！");
    }

    @Override
    public List<MetadataCol> columnInfo(Model model) {
        KuduDatasource kuduDatasource = new KuduDatasource(model.getSourceDatasource());
        KuduModel kuduModel = new KuduModel(model.getProperties(), model.getSourceDatasource());
        String kuduTableName = kuduModel.getKuduTableName();
        return getColumns(kuduDatasource, kuduTableName);
    }
}
