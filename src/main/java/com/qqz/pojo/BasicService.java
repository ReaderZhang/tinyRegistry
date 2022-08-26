package com.qqz.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author qqz @Date:2022/8/25
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BasicService {

    private boolean active;


    private long second;

    public BasicService(long second){
        this.second = second;
        this.active = true;
    }

    public void beat(){
        second = System.currentTimeMillis();
    }

}
