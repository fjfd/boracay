package com.hex.bigdata.udsp.consumer;

import javax.jws.WebMethod;
import javax.jws.WebService;

/**
 * Created by PC on 2017/6/6.
 */
@WebService
public interface WebServiceInterface {

    @WebMethod
    String welcome();

    @WebMethod
    WSResponse consume(WSRequest request);

    @WebMethod
    String consumeJson(String json);

}
