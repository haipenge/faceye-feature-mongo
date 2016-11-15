<%@ include file="/component/core/taglib/taglib.jsp"%>
<%@ page language="java" import="java.util.*" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<div class="page-head">
	<h2>
		<fmt:message key="global.sequence.manage"/>
	</h2>
</div>
<div class="cl-mcont">
	<div class="block-flat" style="padding-top: 0px;">
		<div class="row">
		<div classs="table-responsive">
				<table class="table table-bordered table-hover">
					<tr>
						<td><fmt:message key="global.sequence.name"/></td>
						<td><fmt:message key="global.sequence.value"/></td>
						<!--@generate-entity-jsp-property-desc@-->
						<td><fmt:message key="global.edit"></fmt:message></td>
						<td><fmt:message key="global.remove"></fmt:message></td>
					</tr>
					<c:forEach items="${sequences.content}" var="sequence">
						<tr>
							<td>${sequence.name}</td>
							<td>${sequence.seq}</td>
							<!--@generate-entity-jsp-property-value@-->
							<td><a href="<c:url value="/parse/filterWord/edit/${filterWord.id}"/>"> <fmt:message key="global.edit"></fmt:message>
							</a></td>
							<td><a href="<c:url value="/sequence/remove/${sequence.id}"/>"> <fmt:message key="global.remove"></fmt:message>
							</a></td>
						<tr>
					</c:forEach>
				</table>
			</div>
			
		</div>
	</div>
</div>
