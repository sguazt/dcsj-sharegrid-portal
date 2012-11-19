/*
 * Copyright (C) 2008  Distributed Computing System (DCS) Group, Computer
 * Science Department - University of Piemonte Orientale, Alessandria (Italy).
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package it.unipmn.di.dcs.sharegrid.web.portal.view.test;

import it.unipmn.di.dcs.common.conversion.Convert;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Utility class.
 *
 * @author <a href="mailto:marco.guazzone@gmail.com">Marco Guazzone</a>
 */
public class Util
{
	public static byte[] GetPdfBytes()
	{
		return Convert.Base64ToBytes( PDF_BASE64 );
	}

	public static InputStream getPdfByteStream()
	{
		return new ByteArrayInputStream( GetPdfBytes() );
	}

	public static byte[] GetPngBytes()
	{
		return Convert.Base64ToBytes( PNG_BASE64 );
	}

	public static InputStream getPngByteStream()
	{
		return new ByteArrayInputStream( GetPngBytes() );
	}

	/** Base64 representation of a weaving Duke (in PDF format). */
	private static final String PDF_BASE64 = 
		"JVBERi0xLjQNCiW17a77DQoyIDAgb2JqDQo8PCAvTGVuZ3RoIDMgMCBSDQogICAvRmlsdGVyIC9G"
		+ "bGF0ZURlY29kZQ0KICAgL1R5cGUgL1hPYmplY3QNCiAgIC9TdWJ0eXBlIC9Gb3JtDQogICAvQkJv"
		+ "eCBbIDAgMCAyMjYgNDA3IF0NCj4+DQpzdHJlYW0NCniclVdLaiVHENwP6A69Nky58lefY/gIpmFG"
		+ "C81i7PuDI7Kq+8noyXgQSE1SlZWfiMjUz6Pmz1/fj9//rMf3v1+++Cgjxpx+WLPiQ+aM48cRveiw"
		+ "WuMwjzI8zSElZtQqh6qW5pPXXEo3qdUO6bM4jf04D5MyWq/VD7FWpHeexS0crLUdQ4o0HB2HzIKP"
		+ "9VIZ3eY0XJcBa+UdiSIec7ZD8ZI4v3oxXs4rrSpfvE0nY7f497nRimrnIxHFVBmCIBp35XvhRV0Z"
		+ "LF6OcSUUhiB1pVaLBoz9+FpLby0djM5rfAhGr2k8D63wZlkmmKs3PqvSy2Tw8MqC16qwjVJj8hbe"
		+ "NCbJ1GmOZoxGkXyVwfz41nJqjipq4+GnzTufm99+0fz6KTa+/fby5Y+XLz8/ggnVQnHsUBtFuzPJ"
		+ "H1m7HpFVULS47rYDAVaZGvK6AEIr/vIdjfpoqUy9QtEWKI+w6jJraazEPHQ4wcFHZNQVxIji5qsp"
		+ "DGIQZig7wCDh7KUgreGW1tlY6nSAvs8+swGwdhs8Cx/uRWQlMnvx6OyBmJUgXgBQNCmmZSI6ytRs"
		+ "In4By51JwwcqYPShYAger8pPAYmsdj5j4MOGwrs4DNh/5AIAtB7EpZle8ZFmNepiRRTdGAUP60iQ"
		+ "G0Cmc2yUa7tysY7HESTjeFDChkEBJFsFz2aLogNAZYZZD3TowuREEYBl2AHy+8UG0Gqkj+ZXYw0U"
		+ "1JoNhI9W73p0JUXojhyM0ZkAkgPzerYb1unLis5H5FnGMa/qwQKc1Mz84cPruDogHWlRgkCe2kob"
		+ "unuL1iFbGKEojdxGwYetuPyuxAOG1iBw9Lng2R5ZWGnsFPo3B1TBsmhhSwDx5uaCTbwUCbgziS61"
		+ "0RtT6H2AFTfaaJoqyRVK6FIE61H69Et05o03tIUpoPIlvOVR68BGT6IASaPjeQjSruuSnF2Vd8hm"
		+ "sIP0Qr2r3A7wOQ0jgFAQuwqABwgFpIiwAmVHWamEQyQJ4+1qmMy4xwi5o6mwbAGK2AfrDFNxAC7Z"
		+ "DGIzMbqDlQNiLuavqcED+yxprhdUGSfEYdH8gyw9MZ3PjG+/YHz9RAA/10sI7J5UbIuxPo4Ljnbb"
		+ "YGUpW7tAREjT1BZw6aqD3VPWjdXbCqFsQR4AXjZ4UdNOlqAD+Nz6wK/VeCHuwrZM3UMZPNijEZV1"
		+ "zFm75id5OXmz6UMjvwJdmqGHXio7C1rLJJFuJzJyqG8IPGznu/XgYYWOuqSq9XZRm5q2zoFDnbLD"
		+ "28TS8inMf40IuIy6CqRWavc1hm4jRs8N4ndm7xda7R7G2FcgP8Gq4lZjULo0EPnm5EbUZIZQUJdT"
		+ "Sl1bQwViUCdPHk+7fj43v/2i+fVTUN0olPx5h8KcC7oYBm6Pzv0vR8yCoF9KCMloosTMpFYN1sFb"
		+ "7jIQkNl0zYUgRNYo7pdYdTTUk6St3e7gyKIlhDCNSHLZLKyeuoZRTzYJbnONIpUrNTuRDf1hNAIw"
		+ "eFrOLHJvuDpopvZqjetqv0YbdY1AxydaultOCWV6uXBwal4EwpHuI9UKj+9tgQDZoauvIPG6NMzr"
		+ "mk+AjRwnw681GvJ9bVAf630+sb39b9vr8xb+x5qG6Csnn+Tu5a2txQ57duGmT7EgAPbua/e6oVwD"
		+ "I+cck920hpQA2rZYSBlRKmBDezxTH5jrXL6BHAzNOnO5AUc6dxtE7OTV3o8wygGdtRV2Mp+9iRs8"
		+ "rOKkMMiB3SIWttjjPSlPqoT07C1nxJ5kmDjW1nYySN1UORG5qgA/kOu5QYR8sTjkaeiLcmvBadCK"
		+ "Qeb/Ifi/oPolFB8reR7fsvT/AE26pS4NCmVuZHN0cmVhbQ0KZW5kb2JqDQozIDAgb2JqDQogICAx"
		+ "MzYyDQplbmRvYmoNCjQgMCBvYmoNCjw8IC9GdW5jdGlvblR5cGUgMg0KICAgL0RvbWFpbiBbIDAg"
		+ "MSBdDQogICAvQzAgWyAxIDEgMSBdDQogICAvQzEgWyAwLjk4NDMxNCAwLjc4NDMxNCAwLjcwNTg4"
		+ "MiBdDQogICAvTiAxDQo+Pg0KZW5kb2JqDQo1IDAgb2JqDQo8PCAvRnVuY3Rpb25UeXBlIDINCiAg"
		+ "IC9Eb21haW4gWyAwIDEgXQ0KICAgL0MwIFsgMC45ODQzMTQgMC43ODQzMTQgMC43MDU4ODIgXQ0K"
		+ "ICAgL0MxIFsgMC45ODQzMTQgMC43NjQ3MDYgMC42OTAxOTYgXQ0KICAgL04gMQ0KPj4NCmVuZG9i"
		+ "ag0KNiAwIG9iag0KPDwgL0Z1bmN0aW9uVHlwZSAyDQogICAvRG9tYWluIFsgMCAxIF0NCiAgIC9D"
		+ "MCBbIDAuOTg0MzE0IDAuNzY0NzA2IDAuNjkwMTk2IF0NCiAgIC9DMSBbIDAuOTY4NjI3IDAuNTky"
		+ "MTU3IDAuNTQ1MDk4IF0NCiAgIC9OIDENCj4+DQplbmRvYmoNCjcgMCBvYmoNCjw8IC9GdW5jdGlv"
		+ "blR5cGUgMg0KICAgL0RvbWFpbiBbIDAgMSBdDQogICAvQzAgWyAwLjk2ODYyNyAwLjU5MjE1NyAw"
		+ "LjU0NTA5OCBdDQogICAvQzEgWyAwLjk1Njg2MyAwLjQ0MzEzNyAwLjQxOTYwOCBdDQogICAvTiAx"
		+ "DQo+Pg0KZW5kb2JqDQo4IDAgb2JqDQo8PCAvRnVuY3Rpb25UeXBlIDINCiAgIC9Eb21haW4gWyAw"
		+ "IDEgXQ0KICAgL0MwIFsgMC45NTY4NjMgMC40NDMxMzcgMC40MTk2MDggXQ0KICAgL0MxIFsgMC45"
		+ "NDUwOTggMC4zMjE1NjkgMC4zMTc2NDcgXQ0KICAgL04gMQ0KPj4NCmVuZG9iag0KOSAwIG9iag0K"
		+ "PDwgL0Z1bmN0aW9uVHlwZSAyDQogICAvRG9tYWluIFsgMCAxIF0NCiAgIC9DMCBbIDAuOTQ1MDk4"
		+ "IDAuMzIxNTY5IDAuMzE3NjQ3IF0NCiAgIC9DMSBbIDAuOTM3MjU1IDAuMjI3NDUxIDAuMjM5MjE2"
		+ "IF0NCiAgIC9OIDENCj4+DQplbmRvYmoNCjEwIDAgb2JqDQo8PCAvRnVuY3Rpb25UeXBlIDINCiAg"
		+ "IC9Eb21haW4gWyAwIDEgXQ0KICAgL0MwIFsgMC45MzcyNTUgMC4yMjc0NTEgMC4yMzkyMTYgXQ0K"
		+ "ICAgL0MxIFsgMC45MzMzMzMgMC4xNjA3ODQgMC4xODQzMTQgXQ0KICAgL04gMQ0KPj4NCmVuZG9i"
		+ "ag0KMTEgMCBvYmoNCjw8IC9GdW5jdGlvblR5cGUgMg0KICAgL0RvbWFpbiBbIDAgMSBdDQogICAv"
		+ "QzAgWyAwLjkzMzMzMyAwLjE2MDc4NCAwLjE4NDMxNCBdDQogICAvQzEgWyAwLjkyOTQxMiAwLjEy"
		+ "MTU2OSAwLjE1Mjk0MSBdDQogICAvTiAxDQo+Pg0KZW5kb2JqDQoxMiAwIG9iag0KPDwgL0Z1bmN0"
		+ "aW9uVHlwZSAyDQogICAvRG9tYWluIFsgMCAxIF0NCiAgIC9DMCBbIDAuOTI5NDEyIDAuMTIxNTY5"
		+ "IDAuMTUyOTQxIF0NCiAgIC9DMSBbIDAuOTI5NDEyIDAuMTA5ODA0IDAuMTQxMTc2IF0NCiAgIC9O"
		+ "IDENCj4+DQplbmRvYmoNCjEzIDAgb2JqDQo8PCAvRnVuY3Rpb25UeXBlIDMNCiAgIC9Eb21haW4g"
		+ "WyAwIDEgXQ0KICAgL0Z1bmN0aW9ucyBbIDQgMCBSIDUgMCBSIDYgMCBSIDcgMCBSIDggMCBSIDkg"
		+ "MCBSIDEwIDAgUiAxMSAwIFIgMTIgMCBSIF0NCiAgIC9Cb3VuZHMgWyAwLjA2MDM5NCAwLjA3MTE5"
		+ "OCAwLjE4MjkwNyAwLjI5OTUgMC40MTk5MDcgMC41NDUzMDMgMC42Nzc3OTUgMC44MjIwMDYgXQ0K"
		+ "ICAgL0VuY29kZSBbIDAgMSAwIDEgMCAxIDAgMSAwIDEgMCAxIDAgMSAwIDEgMCAxIF0NCj4+DQpl"
		+ "bmRvYmoNCjE0IDAgb2JqDQo8PCAvVHlwZSAvUGF0dGVybg0KICAgL1BhdHRlcm5UeXBlIDINCiAg"
		+ "IC9NYXRyaXggWyAxIDAgMCAtMSAwIDQwNyBdDQogICAvU2hhZGluZw0KICAgICAgPDwgL1NoYWRp"
		+ "bmdUeXBlIDMNCiAgICAgICAgIC9Db2xvclNwYWNlIC9EZXZpY2VSR0INCiAgICAgICAgIC9Db29y"
		+ "ZHMgWyA4Ni42Mjk4OTggMTY3LjA2OTMwNSAwIDg2LjYyOTg5OCAxNjcuMDY5MzA1IDU0LjcxNTUg"
		+ "XQ0KICAgICAgICAgL0Z1bmN0aW9uIDEzIDAgUg0KICAgICAgICAgL0V4dGVuZCBbIHRydWUgdHJ1"
		+ "ZSBdDQogICAgICA+Pg0KPj4NCmVuZG9iag0KMTUgMCBvYmoNCjw8IC9MZW5ndGggMTYgMCBSDQog"
		+ "ICAvRmlsdGVyIC9GbGF0ZURlY29kZQ0KICAgL1R5cGUgL1hPYmplY3QNCiAgIC9TdWJ0eXBlIC9G"
		+ "b3JtDQogICAvQkJveCBbIDAgMCAyMjYgNDA3IF0NCj4+DQpzdHJlYW0NCnicbZLdatxADIXvDX4H"
		+ "vUC1+p0ZPUGhUGjay9KL4KaFQAKbDfT1K812awfCgncsZB2d880ZTl/uX18fXp5hu8Dp5eHCBpft"
		+ "GU73BL8vsC6sgdyESEEkkDyIGjwBa8fW5ouwomm2dGBxFLaIrBLh6C0igJmyOogEODqKOhHDBmGo"
		+ "NuZ3oaiDIjr0hkOsThyBFjnHoDOy5LulmOPIgeQ5oI2cSzVAxJBNq5zF7ly6ooK1Yup2zWJMF0ZI"
		+ "NI8b9I4xstez3JB7fqUwDHtMO+J86w1G1z43cEPqc8MNmByt4knDjdCIy1rFoGU957ZcnaxMsDpS"
		+ "iykVMa4ZsOa6ZtfNGFttrlCp62h1zLRQfZTy+yw2+LUud+tyBpq/rx//0Ut48GddCD7V43Fdvv+A"
		+ "9A4/18XgM5yBZ/+H+rN0uz29wc0VjlEJv8FdrLz1ueYRd+YnOt3vuIUCe7OCccRdN6bxzHXHLdTR"
		+ "qdewHTdn7jxGJXnEzUNweKvyjpt7CuS+MQ64uVXYdKW14+aENWTO2nGz+613x80uGOLX+3LAzUnR"
		+ "Y0zhHTdbXs/wMnHDXVIZyzWDA25u/v/677g5vTfjqfAuiw2+wd0E/hdrer53DQplbmRzdHJlYW0N"
		+ "CmVuZG9iag0KMTYgMCBvYmoNCiAgIDQ2MA0KZW5kb2JqDQoxNyAwIG9iag0KPDwgL1R5cGUgL1Bh"
		+ "Z2UNCiAgIC9QYXJlbnQgMSAwIFINCiAgIC9NZWRpYUJveCBbIDAgMCAyMjYgNDA3IF0NCiAgIC9D"
		+ "b250ZW50cyBbIDIgMCBSIDE1IDAgUiBdDQogICAvR3JvdXAgPDwNCiAgICAgIC9UeXBlIC9Hcm91"
		+ "cA0KICAgICAgL1MgL1RyYW5zcGFyZW5jeQ0KICAgICAgL0NTIC9EZXZpY2VSR0INCiAgID4+DQo+"
		+ "Pg0KZW5kb2JqDQoxIDAgb2JqDQo8PCAvVHlwZSAvUGFnZXMNCiAgIC9LaWRzIFsgMTcgMCBSIF0N"
		+ "CiAgIC9Db3VudCAxDQogICAvUmVzb3VyY2VzIDw8DQogICAgICAvRXh0R1N0YXRlIDw8DQogICAg"
		+ "ICAgICAvYTAgPDwgL0NBIDEgL2NhIDEgPj4NCiAgICAgID4+DQogICAgICAvUGF0dGVybiA8PCAv"
		+ "cmVzMTQgMTQgMCBSID4+DQogICA+Pg0KPj4NCmVuZG9iag0KMTggMCBvYmoNCjw8IC9DcmVhdG9y"
		+ "IChjYWlybyAxLjQuMTQgKGh0dHA6Ly9jYWlyb2dyYXBoaWNzLm9yZykpDQogICAvUHJvZHVjZXIg"
		+ "KGNhaXJvIDEuNC4xNCAoaHR0cDovL2NhaXJvZ3JhcGhpY3Mub3JnKSkNCj4+DQplbmRvYmoNCjE5"
		+ "IDAgb2JqDQo8PCAvVHlwZSAvQ2F0YWxvZw0KICAgL1BhZ2VzIDEgMCBSDQo+Pg0KZW5kb2JqDQp4"
		+ "cmVmDQowIDIwDQowMDAwMDAwMDAwIDY1NTM1IGYNCjAwMDAwMDQyODYgMDAwMDAgbg0KMDAwMDAw"
		+ "MDAxNyAwMDAwMCBuDQowMDAwMDAxNTI2IDAwMDAwIG4NCjAwMDAwMDE1NTIgMDAwMDAgbg0KMDAw"
		+ "MDAwMTY3OSAwMDAwMCBuDQowMDAwMDAxODI3IDAwMDAwIG4NCjAwMDAwMDE5NzUgMDAwMDAgbg0K"
		+ "MDAwMDAwMjEyMyAwMDAwMCBuDQowMDAwMDAyMjcxIDAwMDAwIG4NCjAwMDAwMDI0MTkgMDAwMDAg"
		+ "bg0KMDAwMDAwMjU2OCAwMDAwMCBuDQowMDAwMDAyNzE3IDAwMDAwIG4NCjAwMDAwMDI4NjYgMDAw"
		+ "MDAgbg0KMDAwMDAwMzE0MiAwMDAwMCBuDQowMDAwMDAzNDQ3IDAwMDAwIG4NCjAwMDAwMDQwNTYg"
		+ "MDAwMDAgbg0KMDAwMDAwNDA4MiAwMDAwMCBuDQowMDAwMDA0NDgyIDAwMDAwIG4NCjAwMDAwMDQ2"
		+ "MTUgMDAwMDAgbg0KdHJhaWxlcg0KPDwgL1NpemUgMjANCiAgIC9Sb290IDE5IDAgUg0KICAgL0lu"
		+ "Zm8gMTggMCBSDQo+Pg0Kc3RhcnR4cmVmDQo0NjczDQolJUVPRg0K";

	/** Base64 representation of a weaving Duke (in PNG format). */
	private static final String PNG_BASE64 =
		"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"
		+ "8v8B92XqftXjtmAAAAAASUVORK5CYII=";
}
