package employees;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "employeesList")
public class EmployeesList {
    private List<Employee> empls;
    private AtomicInteger empId;

    public EmployeesList() {
        empls = new CopyOnWriteArrayList<Employee>();
        empId = new AtomicInteger();
    }


    @XmlElement
    @XmlElementWrapper(name = "employees")
    public List<Employee> getEmployees() {
        return this.empls;
    }

    public void setEmployees(List<Employee> empls) {
        this.empls = empls;
    }


    @Override
    public String toString() {
        String s = "";
        for (Employee e : empls) {
            s += e.toString();
        }
        return s;
    }


    public Employee find(int id) {
        Employee empl = null;
        // Search the list -- for now, the list is short enough that a linear search
        // is ok but binary search would be better if the list increases in an
        // order-of-magnitude larger in size.
        for (Employee e : empls) {
            if (e.getId() == id) {
                empl = e;
                break;
            }
        }
        return empl;
    }

    public int add(String name, String socialSecurityNumber) {
        int id = empId.incrementAndGet();
        Employee e = new Employee();
        e.setName(name);
        e.setSocialSecurityNumber(socialSecurityNumber);
        e.setId(id);
        empls.add(e);
        return id;
    }


}
