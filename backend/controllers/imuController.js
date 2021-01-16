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
		//console.log(req.body);
		
		var imuReading = new imuModel(
            {
                imu_id: req.body.imu_id,
                date : Date.parse(req.body.date),
                direction : req.body.direction
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
        imuModel.find().sort({date:-1}).populate('imu').exec(function (err, imudata) {
            if (err) {
                return res.status(500).json({
                    message: 'Error when getting imu data.',
                    error: err
                });
            }
            return res.status(200).json(imudata);
        });
    },
	
	/**
     * imuController.remove()
     */
    remove: function (req, res) {
        imuModel.remove({}).exec(function (err, data) {
			if (err) {
                return res.status(500).json({
                    message: 'Error when getting imu data.',
                    error: err
                });
            }
            return res.status(200).json({"message": "ok"})
		});
    }
 }