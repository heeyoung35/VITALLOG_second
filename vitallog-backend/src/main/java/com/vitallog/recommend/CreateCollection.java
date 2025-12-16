package com.vitallog.recommend;

import io.milvus.v2.client.MilvusClientV2;
import io.milvus.v2.common.DataType;
import io.milvus.v2.service.collection.request.AddFieldReq;
import io.milvus.v2.service.collection.request.CreateCollectionReq;
import io.milvus.v2.common.IndexParam;

import io.milvus.v2.service.collection.request.GetLoadStateReq;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@SpringBootApplication
public class CreateCollection {
    @Autowired
    private MilvusClientV2 client;

    public void create() {
        try {
            // 1. 컬렉션 스키마 설정
            CreateCollectionReq.CollectionSchema collectionSchema = client.CreateSchema();
            collectionSchema.addField(AddFieldReq.builder().fieldName("nut_no").dataType(DataType.Int64).isPrimaryKey(Boolean.TRUE).autoID(Boolean.FALSE).description("Primary Key").build());
            collectionSchema.addField(AddFieldReq.builder().fieldName("nut_name").dataType(DataType.FloatVector).dimension(768).build());
            collectionSchema.addField(AddFieldReq.builder().fieldName("primary_fncity").dataType(DataType.FloatVector).dimension(768).build());
            collectionSchema.addField(AddFieldReq.builder().fieldName("raw_name").dataType(DataType.FloatVector).dimension(768).build());
            collectionSchema.addField(AddFieldReq.builder().fieldName("warning").dataType(DataType.FloatVector).dimension(768).build());
            collectionSchema.addField(AddFieldReq.builder().fieldName("metadata").dataType(DataType.JSON).build());

            // 2. 인덱스 생성
            List<IndexParam> indexParams = new ArrayList<>();
            indexParams.add(IndexParam.builder().fieldName("nut_name").indexType(IndexParam.IndexType.AUTOINDEX).metricType(IndexParam.MetricType.COSINE).build());
            indexParams.add(IndexParam.builder().fieldName("primary_fncity").indexType(IndexParam.IndexType.AUTOINDEX).metricType(IndexParam.MetricType.COSINE).build());
            indexParams.add(IndexParam.builder().fieldName("raw_name").indexType(IndexParam.IndexType.AUTOINDEX).metricType(IndexParam.MetricType.COSINE).build());
            indexParams.add(IndexParam.builder().fieldName("warning").indexType(IndexParam.IndexType.AUTOINDEX).metricType(IndexParam.MetricType.COSINE).build());

            // 3. 컬렉션 생성
            client.createCollection(CreateCollectionReq.builder().collectionName("supplement").collectionSchema(collectionSchema).indexParams(indexParams).build());

            // 4. Status 확인
            GetLoadStateReq customSetupLoadStateReq1 = GetLoadStateReq.builder().collectionName("supplement").build();

            Boolean loaded = client.getLoadState(customSetupLoadStateReq1);
            System.out.println("Satus : " + loaded);

        } finally {
            client.close(); // 연결 종료
        }
    }
}
