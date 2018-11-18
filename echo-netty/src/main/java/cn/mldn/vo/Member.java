package cn.mldn.vo;

import java.io.Serializable;

public class Member implements Serializable {
    private String mid ;
    private String name ;
    private Integer age ;
    private Double salary ;

    public Member() {}
    public Member(String mid,String name,Integer age,Double salary) {
        this.mid = mid ;
        this.name = name ;
        this.age = age ;
        this.salary = salary ;
    }

    public String getMid() {
        return mid;
    }

    public void setMid(String mid) {
        this.mid = mid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public Double getSalary() {
        return salary;
    }

    public void setSalary(Double salary) {
        this.salary = salary;
    }

    @Override
    public String toString() {
        return "Member{" +
                "mid='" + mid + '\'' +
                ", name='" + name + '\'' +
                ", age=" + age +
                ", salary=" + salary +
                '}';
    }
}
