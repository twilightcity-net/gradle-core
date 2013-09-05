package com.bancvue.zip

import java.util.zip.ZipEntry
import java.util.zip.ZipFile


class ZipArchive {

	private File file
	private ZipFile zip

	ZipArchive(File file) {
		this.file = file
		this.zip = new ZipFile(file)
	}

	String getContentForEntryWithNameLike(String name) {
		ZipEntry entry = zip.entries().find { ZipEntry entry ->
			entry.name =~ /${name}/
		}
		if (entry == null) {
			throw new RuntimeException("No entry with name like '${name}' in file=${file.absolutePath}")
		}
		zip.getInputStream(entry).text
	}
}
