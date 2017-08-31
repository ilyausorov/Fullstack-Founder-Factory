package user_management.play.utils.controller

import play.api.i18n.Messages

object Language {
	def getCurrentLanguage(implicit lang: Messages) = lang.lang.language
}
