package com.example.demo.DropBox;

import lombok.Data;

import java.util.HashMap;

@Data
public class GetRedirectedLinkExecutionResult {
    private HashMap<String, GetRedirectedLinkTask.GetRedirectedLinkResult> successfulResults;

    private HashMap<String, GetRedirectedLinkTask.GetRedirectedLinkResult> failedResults;

    public GetRedirectedLinkExecutionResult(HashMap<String, GetRedirectedLinkTask.GetRedirectedLinkResult> successfulResults,
                                            HashMap<String, GetRedirectedLinkTask.GetRedirectedLinkResult> failedResults){
        this.successfulResults = successfulResults;
        this.failedResults = failedResults;
    }
}
