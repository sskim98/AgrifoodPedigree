this.getIP = function()
{

	var os = require('os')
	var interfaces = os.networkInterfaces();
	var ip_addresse;
	for (k in interfaces) {
		for (k2 in interfaces[k])
		{

			var address = interfaces[k][k2];

			if (address.family == 'IPv4' && !address.internal)
			{
				return address.address;
			}
		}
	}

	return null;

}

