# YOLO3 + Darknet + OpenCV
Označevalnik: https://github.com/tzutalin/labelImg
Darknet: https://pjreddie.com/darknet/install/


## Tutoriali
- [labelImg](https://blog.francium.tech/custom-object-training-and-detection-with-yolov3-darknet-and-opencv-41542f2ff44e)
- [darknet](https://www.arunponnusamy.com/preparing-custom-dataset-for-training-yolo-object-detector.html)

Posodobijo naj se vse konfiguracijske datoteke v /custom_data in /custom_data/cfg
Slike se premaknejo v /custom_data/images
txt datoteke yolo standarda  se premaknejo v /custom_data/labels
custom_data folder se skopira v darknet root install folder
V darknet root install folder se prenese  darknet53.conv.74

### Poženeš

```
darknet$ ./darknet detector train custom_data/detector.data custom_data/cfg/yolov3-custom.cfg darknet53.conv.74
```
Mislim da bi moglo bit okol 100 izpisov vsaj, predn se zaključi, traja pa zelo dolgo (pri meni), naj pač zadnji, kiri bo delo vse to, požene pa pove ka bo.

#### What is the Difference Between Test and Validation Datasets?
>A validation dataset is a sample of data held back from training your model that is used to give an estimate of model skill while tuning model’s hyperparameters.
>The validation dataset is different from the test dataset that is also held back from the training of the model, but is instead used to give an unbiased estimate of the skill of the final tuned model when comparing or selecting between final models.
