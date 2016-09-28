package employees;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlElement;

@XmlRootElement(name = "employee")
public class Employee implements Comparable<Employee> {
    private String name;  // first and last name of employee
    private String socialSecurityNumber; // social secrity number of employee
    private int id; // identifier used as lookup-key

    public Employee() {  }

    @Override
    public String toString() {
        return String.format("%2d: ", id) + name + " ===> " +socialSecurityNumber + "\n";
    }

    // properties
    public void setName(String name) {
        this.name = name;
    }

    @XmlElement
    public String getName() {
        return this.name;
    }

    public void setSocialSecurityNumber(String socialSecurityNumber) {
        this.socialSecurityNumber = socialSecurityNumber;
    }

    @XmlElement
    public String getSocialSecurityNumber() {
        return this.socialSecurityNumber;
    }

    public void setId(int id) {
        this.id = id;
    }

    @XmlElement
    public int getId() {
        return this.id;
    }



    // implementation of Comparable interface
    public int compareTo(Employee other) {
        return this.id - other.id;
    }





}
