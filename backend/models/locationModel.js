var mongoose = require('mongoose');
var Schema   = mongoose.Schema;

var locationScheme = new Schema({
	'zemljepisna_sirina' : Number,
	'zemljepisna_visina' : Number,
    'naziv' : String
});

module.exports = mongoose.model('location', locationScheme); //LOKACIJA
