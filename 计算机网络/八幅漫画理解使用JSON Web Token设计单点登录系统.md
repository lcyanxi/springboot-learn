#### JSON Web Token - 在Web应用间安全地传递信息
JSON Web Token（JWT）是一个非常轻巧的规范。这个规范允许我们使用JWT在用户和服务器之间传递安全可靠的信息。

让我们来假想一下一个场景。在A用户关注了B用户的时候，系统发邮件给B用户，并且附有一个链接“点此关注A用户”。链接的地址可以是这样的

```
https://your.awesome-app.com/make-friend/?from_user=B&target_user=A
```
上面的URL主要通过URL来描述这个当然这样做有一个弊端，那就是要求用户B用户是一定要先登录的。可不可以简化这个流程，让B用户不用登录就可以完成这个操作。JWT就允许我们做到这点。
![image](http://blog.leapoahead.com/2015/09/06/understanding-jwt/jwt.png)

### JWT的组成
一个JWT实际上就是一个字符串，它由三部分组成，头部、载荷与签名

##### 载荷（Payload）
我们先将上面的添加好友的操作描述成一个JSON对象。其中添加了一些其他的信息，帮助今后收到这个JWT的服务器理解这个JWT。

```
{
    "iss": "John Wu JWT",
    "iat": 1441593502,
    "exp": 1441594722,
    "aud": "www.example.com",
    "sub": "jrocket@example.com",
    "from_user": "B",
    "target_user": "A"
}
```
这里面的前五个字段都是由JWT的标准所定义的。
- iss: 该JWT的签发者
- sub: 该JWT所面向的用户
- aud: 接收该JWT的一方
- exp(expires):
- 什么时候过期，这里是一个Unix时间戳
- iat(issued at): 在什么时候签发的

将上面的JSON对象进行[base64编码]可以得到下面的字符串。这个字符串我们将它称作JWT的**Payload**（载荷）。

```
eyJpc3MiOiJKb2huIFd1IEpXVCIsImlhdCI6MTQ0MTU5MzUwMiwiZXhwIjoxNDQxNTk0NzIyLCJhdWQiOiJ3d3cuZXhhbXBsZS5jb20iLCJzdWIiOiJqcm9ja2V0QGV4YW1wbGUuY29tIiwiZnJvbV91c2VyIjoiQiIsInRhcmdldF91c2VyIjoiQSJ9
```
> 小知识：Base64是一种编码，也就是说，它是可以被翻译回原来的样子来的。它并不是一种加密过程。

##### 头部（Header）
JWT还需要一个头部，头部用于描述关于该JWT的最基本的信息，例如其类型以及签名所用的算法等。这也可以被表示成一个JSON对象。

```
{
  "typ": "JWT",
  "alg": "HS256"
}
```
在这里，我们说明了这是一个JWT，并且我们所用的签名算法（后面会提到）是HS256算法。

对它也要进行Base64编码，之后的字符串就成了JWT的**Header**（头部）。

```
eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9
```
##### 签名（签名）
将上面的两个编码后的字符串都用句号.连接在一起（头部在前），就形成了

```
eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJmcm9tX3VzZXIiOiJCIiwidGFyZ2V0X3VzZXIiOiJBIn0
```
最后，我们将上面拼接完的字符串用HS256算法进行加密。在加密的时候，我们还需要提供一个密钥（secret）。如果我们用mystar作为密钥的话，那么就可以得到我们加密后的内容

```
rSWamyAYwuHCo7IFAgd1oRpSP7nzL7BF5t7ItqpKViM
```
这一部分又叫做签名。
![image](http://blog.leapoahead.com/2015/09/06/understanding-jwt/sig1.png)

最后将这一部分签名也拼接在被签名的字符串后面，我们就得到了完整的JWT
```
eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJmcm9tX3VzZXIiOiJCIiwidGFyZ2V0X3VzZXIiOiJBIn0.rSWamyAYwuHCo7IFAgd1oRpSP7nzL7BF5t7ItqpKViM
```
于是，我们就可以将邮件中的URL改成

```
https://your.awesome-app.com/make-friend/?jwt=eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJmcm9tX3VzZXIiOiJCIiwidGFyZ2V0X3VzZXIiOiJBIn0.rSWamyAYwuHCo7IFAgd1oRpSP7nzL7BF5t7ItqpKViM
```

这样就可以安全地完成添加好友的操作了！

且慢，我们一定会有一些问题：
1. 签名的目的是什么？
2. Base64是一种编码，是可逆的，那么我的信息不就被暴露了吗？

让我逐一为你说明。

##### 签名的目的
最后一步签名的过程，实际上是对头部以及载荷内容进行签名。一般而言，加密算法对于不同的输入产生的输出总是不一样的。对于两个不同的输入，产生同样的输出的概率极其地小（有可能比我成世界首富的概率还小）。所以，我们就把“不一样的输入产生不一样的输出”当做必然事件来看待吧。

所以，如果有人对头部以及载荷的内容解码之后进行修改，再进行编码的话，那么新的头部和载荷的签名和之前的签名就将是不一样的。而且，如果不知道服务器加密的时候用的密钥的话，得出来的签名也一定会是不一样的。
![image](http://blog.leapoahead.com/2015/09/06/understanding-jwt/sig2.png)

服务器应用在接受到JWT后，会首先对头部和载荷的内容用同一算法再次签名。那么服务器应用是怎么知道我们用的是哪一种算法呢？别忘了，我们在JWT的头部中已经用alg字段指明了我们的加密算法了。

如果服务器应用对头部和载荷再次以同样方法签名之后发现，自己计算出来的签名和接受到的签名不一样，那么就说明这个Token的内容被别人动过的，我们应该拒绝这个Token，返回一个HTTP 401 Unauthorized响应。
##### 信息会暴露？
是的。

所以，在JWT中，不应该在载荷里面加入任何敏感的数据。在上面的例子中，我们传输的是用户的User ID。这个值实际上不是什么敏感内容，一般情况下被知道也是安全的。

但是像密码这样的内容就不能被放在JWT中了。如果将用户的密码放在了JWT中，那么怀有恶意的第三方通过Base64解码就能很快地知道你的密码了。

##### JWT的适用场景
我们可以看到，JWT适合用于向Web应用传递一些非敏感信息。例如在上面提到的完成加好友的操作，还有诸如下订单的操作等等。

其实JWT还经常用于设计**用户认证**和**授权系**统，**甚至实现Web应用的单点登录**。在下一次的文章中，我将为大家系统地总结JWT在用户认证和授权上的应用

#### 八幅漫画理解使用JSON Web Token设计单点登录系统

上面我们提到了JSON Web Token可以用来设计单点登录系统。我尝试用八幅漫画先让大家理解如何设计正常的用户认证系统，然后再延伸到单点登录系统。

##### 用户认证八步走
所谓用户认证（Authentication），就是让用户登录，并且在接下来的一段时间内让用户访问网站时可以使用其账户，而不需要再次登录的机制。

> 小知识：可别把用户认证和用户授权（Authorization）搞混了。用户授权指的是规定并允许用户使用自己的权限，例如发布帖子、管理站点等。

首先，服务器应用（下面简称“应用”）让用户通过Web表单将自己的用户名和密码发送到服务器的接口。这一过程一般是一个HTTP POST请求。建议的方式是通过SSL加密的传输（https协议），从而避免敏感信息被嗅探。
![image](http://blog.leapoahead.com/2015/09/07/user-authentication-with-jwt/jwtauth1.png)

接下来，应用和数据库核对用户名和密码
![image](http://blog.leapoahead.com/2015/09/07/user-authentication-with-jwt/jwtauth2.png)

核对用户名和密码成功后，应用将用户的id（图中的user_id）作为JWT Payload的一个属性，将其与头部分别进行Base64编码拼接后签名，形成一个JWT。这里的JWT就是一个形**同lll.zzz.xxx的字符串**。

![image](http://blog.leapoahead.com/2015/09/07/user-authentication-with-jwt/jwtauth3.png)

应用将JWT字符串作为该请求Cookie的一部分返回给用户。注意，在这里必须使用HttpOnly属性来防止Cookie被JavaScript读取，从而避免跨站脚本攻击（XSS攻击）。

![image](http://blog.leapoahead.com/2015/09/07/user-authentication-with-jwt/jwtauth4.png)

在Cookie失效或者被删除前，用户每次访问应用，应用都会接受到含有jwt的Cookie。从而应用就可以将JWT从请求中提取出来。

![image](http://blog.leapoahead.com/2015/09/07/user-authentication-with-jwt/jwtauth5.png)

应用通过一系列任务检查JWT的有效性。例如，检查签名是否正确；检查Token是否过期；检查Token的接收方是否是自己（可选）。
![image](http://blog.leapoahead.com/2015/09/07/user-authentication-with-jwt/jwtauth6.png)

应用在确认JWT有效之后，JWT进行Base64解码（可能在上一步中已经完成），然后在Payload中读取用户的id值，也就是user_id属性。这里用户的id为1025。
![image](http://blog.leapoahead.com/2015/09/07/user-authentication-with-jwt/jwtauth7.png)

应用从数据库取到id为1025的用户的信息，加载到内存中，进行ORM之类的一系列底层逻辑初始化。

![image](http://blog.leapoahead.com/2015/09/07/user-authentication-with-jwt/jwtauth8.png)

应用根据用户请求进行响应。
![image](http://blog.leapoahead.com/2015/09/07/user-authentication-with-jwt/jwtauth9.png)

**和Session方式存储id的差异**
Session方式存储用户id的最大弊病在于要占用大量服务器内存，对于较大型应用而言可能还要保存许多的状态。一般而言，大型应用还需要借助一些KV数据库和一系列缓存机制来实现Session的存储。

而JWT方式将用户状态分散到了客户端中，可以明显减轻服务端的内存压力。除了用户id之外，还可以存储其他的和用户相关的信息，例如该用户是否是管理员、用户所在的分桶

虽说JWT方式让服务器有一些计算压力（例如加密、编码和解码），但是这些压力相比磁盘I/O而言或许是半斤八两。具体是否采用，需要在不同场景下用数据说话。

##### 单点登录
Session方式来存储用户id，一开始用户的Session只会存储在一台服务器上。对于有多个子域名的站点，每个子域名至少会对应一台不同的服务器，例如：
- www.taobao.com
- nv.taobao.com
- nz.taobao.com
- login.taobao.com

所以如果要实现在login.taobao.com登录后，在其他的子域名下依然可以取到Session，**这要求我们在多台服务器上同步Session**。

使用JWT的方式则没有这个问题的存在，因为用户的状态已经被传送到了客户端。因此，我们只需要将含有JWT的Cookie的domain设置为顶级域名即可，例如


```
Set-Cookie: jwt=lll.zzz.xxx; HttpOnly; max-age=980000; domain=.taobao.com
```
注意domain必须设置为一个点加顶级域名，即.taobao.com。这样，taobao.com和*.taobao.com就都可以接受到这个Cookie，并获取JWT了。

[原文地址](http://blog.leapoahead.com/2015/09/07/user-authentication-with-jwt/)