tinyMCE.init({
			// General options
			mode : "textareas",
			theme : "advanced",
			plugins : "pagebreak,style,layer,table,save,advhr,advimage,advlink,emotions,iespell,inlinepopups,insertdatetime,preview,media,searchreplace,print,contextmenu,paste,directionality,fullscreen,noneditable,visualchars,nonbreaking,xhtmlxtras,template",

			// Theme options
			theme_advanced_buttons1 : "bold,italic,underline,strikethrough,|,justifyleft,justifycenter,justifyright,justifyfull,styleselect,formatselect,fontselect,fontsizeselect",
			theme_advanced_buttons2 : "cut,copy,paste,|,search,replace,|,bullist,numlist,|,outdent,indent,|,undo,redo,|,forecolor,backcolor",
			theme_advanced_buttons3 : "",
			theme_advanced_buttons4 : "",
			theme_advanced_toolbar_location : "top",
			theme_advanced_toolbar_align : "left",
			theme_advanced_statusbar_location : "",
			theme_advanced_resizing : false,

			// Example content CSS (should be your site CSS)
			project_css : "css/projet.css",
			// Style formats
			style_formats : [ {
				title : 'Bold text',
				inline : 'b'
			}, {
				title : 'Red text',
				inline : 'span',
				styles : {
					color : '#ff0000'
				}
			}, {
				title : 'Red header',
				block : 'h1',
				styles : {
					color : '#ff0000'
				}
			}, {
				title : 'Example 1',
				inline : 'span',
				classes : 'example1'
			}, {
				title : 'Example 2',
				inline : 'span',
				classes : 'example2'
			}, {
				title : 'Table styles'
			}, {
				title : 'Table row 1',
				selector : 'tr',
				classes : 'tablerow1'
			} ],
			// autres options 500X930
			height : '400px',
			width : '730px',
			border: '1px solid #00790E',
			language : "fr"
		});