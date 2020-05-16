var mongoose = require('mongoose');
var Schema   = mongoose.Schema;

var recordingsSchema = new Schema({
	'date' : Date,
	'events': [{
		type: Schema.Types.ObjectId,
		ref: 'events'
	}],
});

module.exports = mongoose.model('recordings', recordingsSchema);
