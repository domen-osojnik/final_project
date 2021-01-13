var express = require('express');
var router = express.Router();
var imageController = require('../controllers/imageController.js');

/*
 * GET
 */
router.get('/', imuController.list);

/*
 * POST
 *  Insert - add img. path to db, store img on server
 */
router.post('/', imuController.insert);

module.exports = router;
