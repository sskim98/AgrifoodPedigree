package kaist.gs1.pms;

import java.util.ArrayList;

// pedigree의 이동경로 정보 타입
public class InfoType_TracePath{
	private String sgtin = "";
	private ArrayList<InfoType_TraceNode> path = null; // pedigree sgtin

	public InfoType_TracePath(String sgtin) {
		this.sgtin = sgtin;
		path = new ArrayList<InfoType_TraceNode>();
	}

	public String getSgtin() {
		return sgtin;
	}

	public void setSgtin(String sgtin) {
		this.sgtin = sgtin;
	}

	public ArrayList<InfoType_TraceNode> getPath() {
		return path;
	}

	public void setPath(ArrayList<InfoType_TraceNode> path) {
		this.path = path;
	}

}
