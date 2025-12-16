package com.vitallog.recommend.repository;

import com.google.gson.JsonObject;
import com.vitallog.config.VectorizationModelConfig;
import com.vitallog.config.ZillizDBConfig;
import io.milvus.v2.client.MilvusClientV2;
import io.milvus.v2.common.DataType;
import io.milvus.v2.common.IndexParam;
import io.milvus.v2.service.collection.request.AddFieldReq;
import io.milvus.v2.service.collection.request.CreateCollectionReq;
import io.milvus.v2.service.collection.request.GetLoadStateReq;
import io.milvus.v2.service.vector.request.InsertReq;
import io.milvus.v2.service.vector.request.SearchReq;
import io.milvus.v2.service.vector.request.data.FloatVec;
import io.milvus.v2.service.vector.response.SearchResp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Repository
public class SupplementVectorRepository {
    private MilvusClientV2 client;
    private ZillizDBConfig zillizDBConfig;
    private VectorizationModelConfig vectorizationModel;

    public SupplementVectorRepository() {}

    @Autowired
    public SupplementVectorRepository(VectorizationModelConfig vectorizationModel, ZillizDBConfig zillizDBConfig, MilvusClientV2 client) {
        this.client = client;
        this.zillizDBConfig = zillizDBConfig;
        this.vectorizationModel = vectorizationModel;
    }

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

    public void insert(List<JsonObject> supplementsList){
        InsertReq insertReq = InsertReq.builder()
            .collectionName("supplement")
            .data(supplementsList)
            .build();

        client.insert(insertReq);
        System.out.println("데이터 삽입 완료!");

        client.close();
    }

    public List<Object> search(float[] vector) {
        FloatVec queryVector = new FloatVec(vector);

        SearchReq searchReq = SearchReq.builder()
            .collectionName("supplement")
            .data(Collections.singletonList(queryVector))
            .annsField("primary_fncity")
            .topK(5)
            .outputFields(List.of("metadata"))
            .build();

        SearchResp searchResp = zillizDBConfig.milvusClient().search(searchReq);

        List<Object> metadataList = new ArrayList<>();
        List<List<SearchResp.SearchResult>> searchResults = searchResp.getSearchResults();
        for (List<SearchResp.SearchResult> results : searchResults) {
            //System.out.println("TopK results:");
            for (SearchResp.SearchResult result : results) {
                metadataList.add(result.getEntity().get("metadata"));
            }
        }
        return metadataList;
    }
}
