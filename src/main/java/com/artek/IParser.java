package com.artek;

import com.jaunt.NotFound;
import com.jaunt.ResponseException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

public interface IParser {
    Map<String, ArrayList<String>> allDepsMarks(Integer userId) throws IOException, ResponseException, NotFound;
}
