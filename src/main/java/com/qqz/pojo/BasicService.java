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

    private String address;

    private int count;
}
