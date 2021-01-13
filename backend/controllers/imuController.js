var mongoose = require('mongoose');
const imuModel = require('../models/imuModel.js');

/**
 * imuController.js
 *
 * @description :: Server-side logic for managing imu data transferred from sensor.
 */

 module.exports = {
     /**
      * imuController.insert()
      * -- Insert data from sensor
      */
     insert : function (req, res, next)
     {
         // Parse recieved data
         var data = JSON.parse(req.body.data);
         console.log(data, "Recieved IMU data");

        var imuReading = new imuModel(
            {
                date : data.date,
                direction : data.direction
            }
        )

        imuReading.save(function (err, result)
        {
            if (err)
            {
                return res.status(500).json({
                    message: 'Error when creating imu reading',
                    error: err
                });
            }
            else
            {
                return res.status(200).json({"Success":"Added event"});
            }
        })
     },
         /**
     * imuController.list()
     */
    list: function (req, res) {
        console.log("MM");
        
        imuModel.find().sort({date:-1}).populate('imu').exec(function (err, imudata) {
            if (err) {
                return res.status(500).json({
                    message: 'Error when getting imu data.',
                    error: err
                });
            }
            return res.json(imudata);
        });
    }
 }