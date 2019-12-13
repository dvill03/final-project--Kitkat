<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" 
           uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<title>KitKat</title>
</head>
<body>
 	${query}
 	<form action="">
 		<input type="text" name="inputQuery" placeholder="search" required>
 		<input type="number" name="numTweets" placeholder="number of tweets" value="20">
 		<input type="submit">
 	</form>

	<c:if test="${not empty tweet_ids}">
				<table>
		<c:forEach items="${tweet_ids}" var="tweet_id">
			<td style="display: inline-block; max-height: 500px;">
	       		<!-- <blockquote class="twitter-tweet tw-align-center"><p lang="en" dir="ltr"> <a href="<c:out value="https://twitter.com/user/status/${tweet_url}"/>" ></a></blockquote> 
			       		<script async src="https://platform.twitter.com/widgets.js" charset="utf-8"></script> -->
				<style>
					#tweet { width: 400px; height: 400px; max-width: 450px; }
					#tweet iframe { order: none !important; box-shadow: none !important; }
				</style>
				<div id="${tweet_id}"></div>
				<script async src="https://platform.twitter.com/widgets.js"></script>
				<script>
					var tweet = document.getElementById("${tweet_id}");
						var id = tweet.getAttribute("${tweet_id}");
						twttr.widgets.createTweet(id, tweet, {
							conversation : 'none',
							cards : 'hidden', 
							linkColor : '#cc0000', 
							theme : 'dark'
						})
				</script>
			</td>
				
		</c:forEach>
				</table>
	</c:if>



</body>
</html>
