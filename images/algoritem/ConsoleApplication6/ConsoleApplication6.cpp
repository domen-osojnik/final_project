#include "pch.h"
#include <iostream>
#include <fstream>
#include <vector>
#include <opencv2/opencv.hpp>
#include <opencv2/dnn/dnn.hpp>
//#include <opencv2/dnn/shape_utils.hpp>
//#include <opencv2/imgproc.hpp>
//#include <opencv2/highgui.hpp>

using namespace std;
using namespace cv;
using namespace dnn;

/*
 *	Functions
 */
vector<String> getOutputsNames(const Net& net);
void printHelpAndExit(int stat);

/*
 *	 Main
 */

int main(int argc, const char* argv[]) {

	// Check input args
	if (argc != 2) printHelpAndExit(-1);

	// Read the image at given file path, 
	Mat img = imread(argv[1], IMREAD_COLOR);

	// Initialize the parameters
	float threshConf = (int)argv[2];	//0.5;		// Confidence threshold [0,1]
	float threshNMS = (int)argv[3];		//0.4;		// Non-maximum suppression threshold [0,1]

	// Load names of classes
	string classesFile = "classes.names";
	ifstream ifs(classesFile.c_str());
	vector<string> classes;
	string line;
	while (getline(ifs, line)) classes.push_back(line);

	// Give the configuration and weight files for the model
	String modelConfiguration = "yolov3.cfg";	// TODO: vkljuciti pot
	String modelWeights = "yolov3.weights";		// TODO: vkljuciti pot

	// Load the network
	Net net = readNetFromDarknet(modelConfiguration, modelWeights);
	net.setPreferableBackend(DNN_BACKEND_OPENCV);
	net.setPreferableTarget(DNN_TARGET_CPU);

	// Create Blob from image 
	Mat blob;
	blobFromImage(img, blob, 1 / 255.0, img.size(), Scalar(0, 0, 0), true, false);

	//Sets the input to the network
	net.setInput(blob);

	// Runs the forward pass to get output of the output layers
	vector<Mat> outs; 
	net.forward(outs, getOutputsNames(net));
	

	// ...
}


/*
 *	Print Help And Exit
 */

void printHelpAndExit(int stat) {
	cout << "OpenCV YOLO3 object detection w/ custom dataset:\n" \
		"Usage:\n" \
		"./detector <[arguments]>\n" \
		"\t<image_path> - path to image to process\n" \
		"\t<confidance_thresh> - confidence threshold [0,1]\n" \
		"\t<model_config_path> - path to model .cfg file\n";
		"\t<model_weights_path> - path to model weights\n";
	exit(stat);
}


// Get the names of the output layers
vector<String> getOutputsNames(const Net& net) {
	static vector<String> names;
	if (names.empty())
	{
		//Get the indices of the output layers, i.e. the layers with unconnected outputs
		vector<int> outLayers = net.getUnconnectedOutLayers();

		//get the names of all the layers in the network
		vector<String> layersNames = net.getLayerNames();

		// Get the names of the output layers in names
		names.resize(outLayers.size());
		for (size_t i = 0; i < outLayers.size(); ++i)
			names[i] = layersNames[outLayers[i] - 1];
	}
	return names;
}
