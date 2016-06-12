package com.showjoy.tashow.utils;

import java.util.ArrayList;
import java.util.List;

public class ArrayUtils {
	public static <T> List<T> arrayToList(T[] params) {
		if (params == null || params.length == 0) {
			return new ArrayList<T>();
		}
		List<T> result = new ArrayList<T>();
		for (T t : params) {
			result.add(t);
		}
		return result;
	}
}
