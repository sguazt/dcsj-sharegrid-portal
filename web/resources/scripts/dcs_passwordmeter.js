/// Password strength rater.
///
/// Author: Marco Guazzone (marco.guazzone@gmail.com)
///
function dcs_updatePasswordStrength(nicknameId, emailId, passwdId, passwdStrengthId, passwdStrengthTextId)
{
	var nicknameCtl = document.getElementById( nicknameId );
	var emailCtl = document.getElementById( emailId );
	var passwdCtl = document.getElementById( passwdId );
	var passwdStrengthCtl = document.getElementById( passwdStrengthId );
	var passwdStrengthTextCtl = document.getElementById( passwdStrengthTextId );

	if ( passwdStrengthCtl == undefined || passwdStrengthTextCtl == undefined )
	{
		// No password strength control defined
		return;
	}

	var password = passwdCtl.value;
	var strength = 0;

	// easyGuesses: strings that should not be used in password
	var easyGuesses = new Array();
	easyGuesses.push( '12345678' );
	easyGuesses.push( 'password' );
	easyGuesses.push( 'sharegrid' );
	easyGuesses.push( 'sgportal' );
	easyGuesses.push( emailCtl.value.toLowerCase() );
	// Split contiguous words contained in the email and reconcat
	var email_words = emailCtl.value.toLowerCase().match( /\w+/g );
	if ( email_words )
	{
		easyGuesses.push( email_words.concat() );
	}
	if ( nicknameCtl.value )
	{
		easyGuesses.push( nicknameCtl.value.toLowerCase() );
	}

	locase_matches = password.match( /[a-z_]/g ); // lowercase and '_' matches
	digit_matches = password.match( /[0-9]/g ); // numeric matches
	upcase_matches = password.match( /[A-Z]/g ); // uppercase matches
	special_matches = password.match( /\W/g ); // special matches (not in a-z, A-Z, 0-9, _)

	/// For passwords long less than or equal to 5 characters, leave
	/// strength at 0 since they are too short.
	/// For those greater than 5 characters, rate according to the
	/// following metrics:
	/// * 1 more point for each character exceeding the length of 5.
	/// * 1 more point for each mix of upper/lower case characters.
	/// * 1 more point for each mix of number and upper/lower case
	///   characters.
	/// * 1 more point for each special character.
	/// * 2 bonus point for each mix of number/special/lower/upper
	///   characters

	if ( password.length > 5 )
	{
		// 1 point for each character more than 5
		strength += password.length - 5;

		// 1 point for each upper-case character mixed with lower-case
		if ( locase_matches && upcase_matches )
		{
			strength += upcase_matches.length;
		}

		// 1 point for each numeric character mixed with
		// lower/upper-case
		if ( ( upcase_matches || locase_matches ) && digit_matches )
		{
			strength += digit_matches.length;
		}

		// 1 point for each special characters
		if ( special_matches )
		{
			strength += special_matches.length;
		}

		// 2 bonus points if mix of letters, numbers and special
		if ( ( locase_matches || upcase_matches ) && special_matches && digit_matches )
		{
			strength += 2;
		}
	}

	/// Reset strength to 0 if any easy guess in password (easy guess should
	/// be more than 3 chars)

	// Make  ease guess case insensitive
	var passwdLoCase = password.toLowerCase();
	for ( var i=0; i < easyGuesses.length; ++i )
	{
		if ( easyGuesses[i].length > 3 && ( passwdLoCase.indexOf( easyGuesses[i] ) != -1 ) )
		{ 
			strength = 0;
			break;
		}
	}

	var pstrength_elem = passwdStrengthCtl;
	var pstrength_text = passwdStrengthTextCtl;
	if ( password.length == 0 )
	{
		pstrength_elem.className = 'dcs-sg-passwordStrengthEmpty';             
		pstrength_text.innerHTML = 'None'; // no i18n
	}
	else if ( strength < 3 )
	{
		pstrength_elem.className = 'dcs-sg-passwordStrengthWeak';
		pstrength_text.innerHTML = 'Weak'; // no i18n
	}
	else if ( strength < 7 )
	{
		pstrength_elem.className = 'dcs-sg-passwordStrengthFair';
		pstrength_text.innerHTML = 'Fair'; // no i18n
	}
	else if ( strength < 10 )
	{
		pstrength_elem.className = 'dcs-sg-passwordStrengthGood';
		pstrength_text.innerHTML = 'Good'; // no i18n
	}
	else
	{
		pstrength_elem.className = 'dcs-sg-passwordStrengthStrong';
		pstrength_text.innerHTML = 'Strong'; // no i18n
	}
}
