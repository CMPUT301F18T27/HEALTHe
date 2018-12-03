package team27.healthe.controllers;

import org.elasticsearch.common.unit.DistanceUnit;
import org.elasticsearch.index.query.FilterBuilders;
import org.elasticsearch.index.query.GeoDistanceFilterBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;

import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;

// For performing searches with elastic search
public class ElasticSearchSearchController extends ElasticSearchController {

    public SearchResult searchGeneral(String terms) {
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.multiMatchQuery(terms, "title", "description"));
        searchSourceBuilder.sort(SortBuilders.scoreSort().order(SortOrder.ASC));

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

    public SearchResult searchGeoLocation(Double lat, Double lon, Double distance) {
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.matchAllQuery());

        GeoDistanceFilterBuilder filter = FilterBuilders.geoDistanceFilter("geoLocation.location").point(lat, lon)
                .distance(distance, DistanceUnit.KILOMETERS);
        searchSourceBuilder.filter(filter).sort(SortBuilders.geoDistanceSort("geoLocation.location").point(lat, lon).order(SortOrder.ASC));


        Search search = new Search.Builder(searchSourceBuilder.toString())
                // multiple index or types can be added.
                .addIndex(test_index)
                //.addType(problem_type)
                .addType(record_type)
                .build();


        try {
            SearchResult result = client.execute(search);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    public SearchResult searchBodyLocation(String terms) {
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.multiMatchQuery(terms, "bodyLocation.body_string"));
        searchSourceBuilder.sort(SortBuilders.scoreSort().order(SortOrder.ASC));

        Search search = new Search.Builder(searchSourceBuilder.toString())
                // multiple index or types can be added.
                .addIndex(test_index)
                //.addType(problem_type)
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
