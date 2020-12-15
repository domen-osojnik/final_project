#include <iostream>
#include <vector>
#include <fstream>
#include <mpi.h>
#include <sstream>
#include <iterator>

using namespace std;

const int TAG = 0;
const int MASTER = 0;
const int WORKER_1 = 1;
const int WORKER_2 = 2;
const int WORKER_3 = 3;

int main(int argc, char** argv) {
    MPI_Init(&argc, &argv);
    int processNumber, processRank;
    MPI_Status processStatus;

    MPI_Comm_size(MPI_COMM_WORLD, &processNumber);
    MPI_Comm_rank(MPI_COMM_WORLD, &processRank);

    const clock_t start = clock();

    if (processRank == 0) {
        // Handle input
        
        // Preberi datoteko
        string infile = "out.txt";
        // Vhodni podatki
        vector<float> st_vozil, hitrost, razmik;
        int readCnt = 0;
        int total_values = 0;
        std::fstream myfile(infile, std::ios_base::in);
        float number;
        
        vector<float> values;
        
        while (myfile >> number) {
            values.push_back(number);
        }
        
        for (int z = 1; z < values[0]; z++){
        switch (readCnt) {
            case 0:
                st_vozil.push_back(values[z]);
                break;
            case 1:
                hitrost.push_back(values[z]);
                break;
            case 2:
                razmik.push_back(values[z]);
                break;
                
            default:
                break;
        }
        if (readCnt == 2)
            readCnt = 0;
        else
            readCnt ++;
        }
        
        int size1 = st_vozil.size();
        // Send the size of vector first
        MPI_Send(&size1, 1, MPI_INT, WORKER_1, TAG, MPI_COMM_WORLD);
        // Send to first process to count all the cars
        MPI_Send(&st_vozil.front(), st_vozil.size(), MPI_FLOAT, WORKER_1, TAG, MPI_COMM_WORLD);
        
        // TODO
        // Send the size of vector first
        // Send to second process...
        
        
        // TODO
        // Send the size of vector first
        // Send to third process...
        
        
        // TODO
        // Handle the results
        
        int result1, result2, result3;
        // Handle first result (from worker 1)
        MPI_Recv(&result1, 1, MPI_INT, WORKER_1, TAG, MPI_COMM_WORLD, &processStatus);
        
        // Handle second result (from worker 2)
        //MPI_Recv(&result2, 1, MPI_INT, WORKER_2, TAG, MPI_COMM_WORLD, &processStatus);
        
        // Handle third result (from worker 3)
        //MPI_Recv(&result3, 1, MPI_INT, WORKER_3, TAG, MPI_COMM_WORLD, &processStatus);
        
        
        // Ko dobi vse rezultate, jih izpise
        float time = float(clock() - start) / CLOCKS_PER_SEC;
        
        cout << "Stevilo vseh avtomobilov na cesti v pretekli uri v Sloveniji : " << result1 << endl;
        //TODO OSTALI REZULTATI...
        //...
        
        cout << "Pretekel cas: " << time << endl;
    }
    
    if (processRank == 1) {
        int size;
        MPI_Recv(&size, 1, MPI_INT, MASTER, TAG, MPI_COMM_WORLD, &processStatus);
        vector<float> numOfCars;
        numOfCars.resize(size);
        MPI_Recv(&numOfCars[0], size, MPI_FLOAT, MASTER, TAG, MPI_COMM_WORLD, &processStatus);
        int sum;
        
        // Calculate result
        for (int i = 0; i<size; i++) {
            sum+=numOfCars[i];
        }
        
        // Send result back to master
        MPI_Send(&sum, 1, MPI_INT, MASTER, TAG, MPI_COMM_WORLD);
    }

    MPI_Finalize();
}
