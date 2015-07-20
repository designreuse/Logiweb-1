<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<jsp:include page="../GlobalHeader.jsp">
	<jsp:param name="title" value="Add driver" />
</jsp:include>

<jsp:include page="../GlobalHeaderMenu.jsp">
	<jsp:param name="homeLink" value="/manager" />
	<jsp:param name="userRoleForTitle" value="Manager" />
</jsp:include>

<form:form modelAttribute="driverModel" method="post" cssClass="form-horizontal">
	<fieldset>

		<!-- Form Name -->
		<legend>Add driver</legend>

		<%--Error message --%>
		<c:if test="${not empty error}">
			<div class="form-group">
				<div class="col-md-4">
				</div>
				<div class="col-md-4 alert alert-warning">
					<strong>Warning!</strong> ${error}
					<form:errors path="*"/>
				</div>
			</div>
		</c:if>

        <spring:bind path="employeeId">
          <div class="form-group ${status.error ? 'has-error' : ''}">
            <label class="col-md-4 control-label">Employee Id</label>
            <div class="col-md-4">
                <form:input path="employeeId" type="text" class="form-control input-md" 
                                id="employeeId" placeholder="Employee Id" />
                <form:errors path="employeeId" class="control-label has-error" />
            </div>
          </div>
        </spring:bind>
        
		<spring:bind path="name">
          <div class="form-group ${status.error ? 'has-error' : ''}">
            <label class="col-md-4 control-label">Name</label>
            <div class="col-md-4">
                <form:input path="name" type="text" class="form-control input-md" 
                                id="name" placeholder="Name" />
                <form:errors path="name" class="control-label has-error" />
            </div>
          </div>
        </spring:bind>
        
        <spring:bind path="surname">
          <div class="form-group ${status.error ? 'has-error' : ''}">
            <label class="col-md-4 control-label">Surname</label>
            <div class="col-md-4">
                <form:input path="surname" type="text" class="form-control input-md" 
                                id="surname" placeholder="Surname" />
                <form:errors path="surname" class="control-label" />
            </div>
          </div>
        </spring:bind>
        
        <spring:bind path="currentCityId">
          <div class="form-group ${status.error ? 'has-error' : ''}">
            <label class="col-md-4 control-label">City</label>
            <div class="col-md-4">
                <form:select path="currentCityId" class="form-control">
                    <form:options items="${cities}" itemLabel="name" itemValue="id"  />
                </form:select>
                <form:errors path="currentCityId" class="control-label" />
            </div>
          </div>
        </spring:bind>
        
		<!-- Submit -->
		<div class="form-group">
			<label class="col-md-4 control-label"></label>
			<div class="col-md-4">
				<button class="btn btn-primary" type="submit">Create Driver</button>
			</div>
		</div>

	</fieldset>
</form:form>


<jsp:include page="../GlobalFooter.jsp" />