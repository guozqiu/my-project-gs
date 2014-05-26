package com.lidroid.xutils.util;

import com.lidroid.xutils.db.sqlite.Cursor;

public class IOUtils {

	public static void closeQuietly(Cursor cursor) {
		cursor.close();
	}

}
