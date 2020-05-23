var express = require('express');
var router = express.Router();
var imageController = require('../controllers/imageController.js');

/*
 * GET
 */
router.get('/', imageController.list);

/*
 * POST
 *  Insert - add img. path to db, store img on server
 */
router.post('/', imageController.insert);

/*
 * DELETE
 *  Clear - delete all photos (testing)
*/
router.delete('/', imageController.clear);

module.exports = router;
