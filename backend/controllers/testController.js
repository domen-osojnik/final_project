var testModel = require('../models/testModel.js');

/**
 * testController.js
 *
 * @description :: Server-side logic for managing tests.
 */
module.exports = {

    /**
     * testController.test()
     */
    test: function (req, res) {
        var test = new testModel({
			msg : req.body.msg
        });

        console.log(test.msg);
        return res.status(200).json({"msg":"ok"});

    },
 
};
