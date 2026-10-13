package com.chettra.devflow.common.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ProjectNameNormalizer {
	public String normalize(String name){
		if (name == null){
			return null;
		}
		return name.trim().replaceAll("\\s+"," ");
	}
}
