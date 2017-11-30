package com.wisdom.common.activiti.model.vo;

import java.util.List;
import java.util.Map;



/**
 * 
 * @author shangl
 * @description  下拉框查询项
 * @date   2017/9/15
 *
 */

public class SelectDataVO {

    private String id;
    
    private String text;
    
    private String selected;	   //是否默认值
    
    private Map<String,String>    attribute;     //下拉框属性

    private List<SelectDataVO> children;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getSelected() {
        return selected;
    }

    public void setSelected(String selected) {
        this.selected = selected;
    }

    public List<SelectDataVO> getChildren() {
        return children;
    }

    public void setChildren(List<SelectDataVO> children) {
        this.children = children;
    }

    public Map<String,String> getAttribute() {
        return attribute;
    }

    public void setAttribute(Map<String,String> attribute) {
        this.attribute = attribute;
    }
    
    
    
    
    
    
}
