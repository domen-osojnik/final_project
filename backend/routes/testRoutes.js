var express = require('express');
var router = express.Router();
var testController = require('../controllers/testController.js');

/*
 * POST
 */
router.post('/', testController.test);

module.exports = router;
