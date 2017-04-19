package kaist.gs1.pms;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
 * spring 에서 외부 shell command를 실행할 수 있는지 테스트 하기 위한 루틴, 현재 사용하지 않음
 */
public class MyShellCommand {

	private static final String IPADDRESS_PATTERN = "([01]?\\d\\d?|2[0-4]\\d|25[0-5])" 
		+ "\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])" 
		+ "\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])" 
		+ "\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])";

	private static Pattern pattern = Pattern.compile(IPADDRESS_PATTERN);
	private static Matcher matcher;

	public String executeCommand(String command) {

		StringBuffer output = new StringBuffer();

		Process p;
		try {
			p = Runtime.getRuntime().exec(command);
			p.waitFor();
			BufferedReader reader = 
                           new BufferedReader(new InputStreamReader(p.getInputStream()));

			String line = "";			
			while ((line = reader.readLine())!= null) {
				output.append(line + "\n");
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return output.toString();

	}

	public List<String> getIpAddress(String msg) {

		List<String> ipList = new ArrayList<String>();

		if (msg == null || msg.equals(""))
			return ipList;

		matcher = pattern.matcher(msg);
		while (matcher.find()) {
			ipList.add(matcher.group(0));
		}

		return ipList;
	}
}