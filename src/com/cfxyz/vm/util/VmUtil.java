package com.cfxyz.vm.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * 验证字符串的合法性
 *
 */
public final class VmUtil {
	private VmUtil() {
	}

	/**
	 * 验证字符串是否为整数
	 * 
	 * @param str
	 * @return
	 */
	public static boolean validateInteger(String str) {
		if (str.matches("-?\\d+")) {
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * 从文件中读取源代码
	 * @param filePath
	 * @return 源代码字符串
	 */
	public static String loadLib(String filePath) {
		String sourceTxt = "" ;
		try {
			String encoding = "UTF-8";
			File file = new File(filePath);
			if (file.isFile() && file.exists()) { // 判断文件是否存在
				InputStreamReader read = new InputStreamReader(new FileInputStream(file), encoding);// 考虑到编码格式
				BufferedReader bufferedReader = new BufferedReader(read);
				String lineTxt = null;
				while ((lineTxt = bufferedReader.readLine()) != null) {
						sourceTxt += " " + lineTxt;  //全部读取完文件后才运行
				}
				read.close();
			} else {
				System.out.println("找不到指定的文件");
			}
		} catch (Exception e) {
			System.out.println("读取文件内容出错");
			e.printStackTrace();
		}
		return sourceTxt ;
	}

	/**
	 * 将一行源代码字符串经过预处理
	 * 
	 * @param line
	 * @return 返回Forth词列表
	 */
	public static List<String> separateWord(String line) {
		List<String> source = new ArrayList<String>();

		List<Integer> note = new ArrayList<Integer>();
		note.add(0);
		int start = 0;
		int end = 0;
		if (line.trim().equals("")) {
			source.add("END");
		} else if (line.indexOf(".\" ") != -1) {
			while (true) {
				if (line.substring(end).indexOf(".\" ") != -1) {
					start = line.substring(end).indexOf(".\" ") + end + 3;
					end = line.substring(start).indexOf("\"") + start + 1;
					if (end != -1) {
						note.add(start);
						note.add(end);

					} else {
						break;
					}
				} else {
					break;
				}
			}
			note.add(line.length());
			for (String s : line.substring(note.get(0), note.get(1)).trim().split("\\s+")) {
				source.add(s);
			}
			for (int x = 1; x < note.size() - 2; x += 2) {
				int y = x + 1;
				int z = x + 2;

				source.add(line.substring(note.get(x), note.get(y)).trim());
				for (String s : line.substring(note.get(y), note.get(z)).trim().split("\\s+")) {
					if (!s.equals("")) {
						source.add(s);
					}
				}
			}

		} else {
			for (String x : line.trim().split("\\s+")) {
				source.add(x);
			}
		}

		return source;
	}
}
