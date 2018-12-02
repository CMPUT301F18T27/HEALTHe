package team27.healthe.controllers;

import android.util.Log;

import com.google.gson.Gson;

import io.searchbox.client.JestResult;
import io.searchbox.core.Delete;
import io.searchbox.core.DocumentResult;
import io.searchbox.core.Get;
import io.searchbox.core.Index;
import team27.healthe.model.Problem;

public class ProblemElasticSearchController extends ElasticSearchController {

    /**
     * Add problem to elastic search database using problem id as the id in elastic search
     * @param p (Problem Class)
     */
    public static void addProblem(Problem p) {
        verifyClient();

        Gson gson = new Gson();
        String problem_json = gson.toJson(p);

        Index index = new Index.Builder(problem_json).index(test_index).type(problem_type).id(p.getProblemID()).build();

        try {
            client.execute(index);
        }
        catch (Exception e) {
            Log.i("Error", e.toString());
        }
    }

    /**
     * Get the problem from a given problem id
     * @param problem_id (Integer)
     * @return Problem (Class)
     */
    public static Problem getProblem(String problem_id) {
        verifyClient();
        Get get = new Get.Builder(test_index, problem_id).type(problem_type).build();

        try {
            JestResult result = client.execute(get);

            Gson gson = new Gson();
            Problem problem = gson.fromJson(result.getSourceAsString(),Problem.class);
            return problem;
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Removes a specified problem for the given user (corresponding to user_id)
     * @param problem_id (Integer)
     */
    public static void removeProblem(String problem_id){
        verifyClient();
        try{
            client.execute(new Delete.Builder(problem_id)
                    .index(test_index)
                    .type(problem_type)
                    .build());
        } catch (Exception e){
            e.printStackTrace();
        }

    }
}
