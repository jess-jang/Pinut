package com.mindlinksw.schoolmeals.model


data class NaverLogin(var resultcode: String,
                      var message: String,
                      var response: Response)


data class Response(var id: String,
                    var nickname: String,
                    var profile_image: String,
                    var age: String,
                    var gender: String,
                    var email: String,
                    var name: String,
                    var birthday: String)

