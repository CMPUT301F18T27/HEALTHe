package team27.healthe.controllers;

import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;

import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;

// For performing searches with elastic search
public class ElasticSearchSearchController extends ElasticSearchController {

    public SearchResult searchGeneral(String terms) {
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.multiMatchQuery(terms, "title", "description"));

        Search search = new Search.Builder(searchSourceBuilder.toString())
                // multiple index or types can be added.
                .addIndex(test_index)
                .addType(problem_type)
                .addType(record_type)
                .build();
        try {
            return client.execute(search);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
