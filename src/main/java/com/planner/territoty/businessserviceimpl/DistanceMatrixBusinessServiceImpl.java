package com.planner.territoty.businessserviceimpl;

import com.planner.territoty.Customer;
import com.planner.territoty.businessservice.DistanceMatrixBusinessService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DistanceMatrixBusinessServiceImpl implements DistanceMatrixBusinessService {
    @Override
    public List<Customer> calculateOutletDistance(List<Customer> customerList) {
        int size = customerList.size();
        double arr[][] = new double[size][size];
        for (int i = 0; i < customerList.size(); i++) {
            double sum = 0;
            for (int j = 0; j < customerList.size(); j++) {
                Customer first = customerList.get(i);
                // System.out.println(first);
                Customer second = customerList.get(j);
                // System.out.println(second);
                double DistancePoint = (((second.getLattitude() -first.getLattitude())*(second.getLattitude()-first.getLattitude()  )) + ((second.getLongitude() - first.getLongitude())*(second.getLongitude() - first.getLongitude())));
                double distance = Math.sqrt(DistancePoint)*100;
                sum += distance;
                System.out.println("for "+first.getCustomerCode()+" >>"+first.getLattitude()+"  "+ first.getLongitude() +" to "+second.getCustomerCode()+" >>"+second.getLattitude()+"  "+ second.getLongitude()+" is "+distance);
                arr[i][j] = distance;
                List<Customer> customerLst = customerList.get(i).getCustomerList();
                customerLst.add(second);
                customerLst.get(i).setCustomerList(customerLst);
            }
            customerList.get(i).setDistance(sum);
            System.out.println("DOne done done");
        }
        // Distance calculate and store id in matrix//
        // int arr[][] = new int[2][2]; //minimum distance sequence
        System.out.println(customerList);
        for (int i = 0; i < customerList.size(); i++) {
            for (int j = 0; j < customerList.size(); j++) {
                System.out.println("  "+arr[i][j]+"  ");
            }
            System.out.println("  ");
        }
        return customerList;
    }
}
