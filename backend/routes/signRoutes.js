var express = require('express');
var router = express.Router();
var signController = require('../controllers/signController.js');

/*
 * GET
 */
router.get('/', signController.list);

/*
 * POST
 *  Insert - add img. path to db, store img on server
 */

/*
 * DELETE
 *  Clear - delete all photos (testing)
*/
router.delete('/', signController.clear);

module.exports = router;
