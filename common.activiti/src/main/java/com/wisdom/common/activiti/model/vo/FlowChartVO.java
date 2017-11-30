package com.wisdom.common.activiti.model.vo;


/**
 * 
 * @author shangl
 * @description 流程图使用对象
 * @date   2017/8/9
 *
 */
@SuppressWarnings("unused")
public class FlowChartVO {
    	
    	private static final long serialVersionUID = 1L;
    	
    	private String deploymentId;  //部署流程ID    	
    	private String chartName;     //流程图名字
	
    	private String pathX;         //x
    	private String pathY;         //y
    	private String pathHeight;    //高
    	private String pathWidth;     //宽
	public String getDeploymentId() {
	    return deploymentId;
	}
	public void setDeploymentId(String deploymentId) {
	    this.deploymentId = deploymentId;
	}
	public String getChartName() {
	    return chartName;
	}
	public void setChartName(String chartName) {
	    this.chartName = chartName;
	}
	public String getPathX() {
	    return pathX;
	}
	public void setPathX(String pathX) {
	    this.pathX = pathX;
	}
	public String getPathY() {
	    return pathY;
	}
	public void setPathY(String pathY) {
	    this.pathY = pathY;
	}
	public String getPathHeight() {
	    return pathHeight;
	}
	public void setPathHeight(String pathHeight) {
	    this.pathHeight = pathHeight;
	}
	public String getPathWidth() {
	    return pathWidth;
	}
	public void setPathWidth(String pathWidth) {
	    this.pathWidth = pathWidth;
	}
    	
    	
    	
    	
    	
    
    

}
