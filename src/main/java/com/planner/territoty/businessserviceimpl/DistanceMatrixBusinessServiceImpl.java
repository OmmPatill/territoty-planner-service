package com.planner.territoty.businessserviceimpl;

import com.planner.territoty.Customer;
import com.planner.territoty.businessservice.DistanceMatrixBusinessService;
import com.planner.territoty.entity.DistanceMatrix;
import com.planner.territoty.repository.DistanceMatrixRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class DistanceMatrixBusinessServiceImpl implements DistanceMatrixBusinessService {

    List<DistanceMatrix> distanceMatrixList = new ArrayList<>();

    @Autowired
    DistanceMatrixRepository distanceMatrixRepository;
    @Override
    public List<Customer> calculateOutletDistance(List<Customer> customerList) {
        int size = customerList.size();
        double arr[][] = new double[size][size];
        for (int i = 0; i < customerList.size(); i++) {
            double sum = 0;
            Customer first = customerList.get(i);
            for (int j = 0; j < customerList.size(); j++) {

                // System.out.println(first);
                Customer second = customerList.get(j);
                // System.out.println(second);
                double DistancePoint = (((second.getLattitude() -first.getLattitude())*(second.getLattitude()-first.getLattitude()  )) + ((second.getLongitude() - first.getLongitude())*(second.getLongitude() - first.getLongitude())));
                double distance = Math.sqrt(DistancePoint)*100;
                sum += distance;
                System.out.println("for "+first.getCustomerCode()+" >>"+first.getLattitude()+"  "+ first.getLongitude() +" to "+second.getCustomerCode()+" >>"+second.getLattitude()+"  "+ second.getLongitude()+" is "+distance);
                arr[i][j] = distance;

                DistanceMatrix d = toDistanceMatrix(first, second, distance);
                distanceMatrixList.add(d);

            }
            customerList.get(i).setDistance(sum);
            System.out.println("DOne done done");
        }

        distanceMatrixRepository.saveAllAndFlush(distanceMatrixList);
        distanceMatrixList = new ArrayList<>();
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

    private DistanceMatrix toDistanceMatrix(Customer first, Customer second, double distance) {

        DistanceMatrix d = new DistanceMatrix();
        d.setFromConsumerCode(first.getCustomerCode());
        d.setToConsumerCode(second.getCustomerCode());
        d.setDistance(distance);
        d.setDistributorCode("");

        return d;
    }
}
