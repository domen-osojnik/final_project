var mongoose = require('mongoose');
var Schema   = mongoose.Schema;

var imuSchema = new Schema({
    'imu_id' : Schema.Types.ObjectId,
    'date' : Date,
    'direction' : String,
});

module.exports = mongoose.model('imu', imuSchema);