var express = require('express');
var router = express.Router();
var imuController = require('../controllers/imuController.js');

/*
 * GET
 * Get list of inserted sensor data
 */
router.get('/', imuController.list);

/*
 * POST
 *  Insert - add IMU data in form of {id, date, direction} to database
 */
router.post('/', imuController.insert);


/*
 * DELETE
 */
router.delete('/', imuController.remove);

module.exports = router;
