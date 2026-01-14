# Quick Reference Card - Brac Bank Interview

## 🎯 **Elevator Pitch (30 seconds)**
*"I built a production-ready money transfer application using Spring Boot 3.5.9 with Java 21, implementing ACID transactions, JWT security with refresh token rotation, and comprehensive audit trails. The system uses PostgreSQL with HikariCP connection pooling and follows banking industry best practices for security and data integrity."*

## 🔑 **Key Technical Points**

### **Architecture**
- **Layered Architecture**: Controller → Service → Repository → Database
- **Separation of Concerns**: Each layer has distinct responsibilities
- **Dependency Injection**: @Autowired for loose coupling

### **Security**
- **JWT + Refresh Token Rotation**: Enterprise-grade authentication
- **BCrypt Password Hashing**: Industry standard (adaptive hashing)
- **@Transactional**: ACID compliance for financial operations

### **Database**
- **HikariCP**: Fastest connection pool (Spring Boot default)
- **PostgreSQL**: ACID compliance, perfect for financial data
- **NUMERIC(15,2)**: Exact precision for money calculations

### **Spring Boot Benefits**
- **Auto-Configuration**: Automatic setup based on dependencies
- **Embedded Tomcat**: No external server needed
- **Starter Dependencies**: Consistent, tested dependency combinations

## 🚀 **Demo Flow**
1. **Login** → Show dual token response
2. **Transfer Money** → Demonstrate ACID transaction
3. **Check History** → Show audit trail
4. **Refresh Token** → Show token rotation
5. **Logout** → Show token revocation

## 💡 **Common Answers**

**"Why HikariCP?"**
*"Spring Boot's default choice after extensive benchmarking. Fastest performance, lowest memory usage, and excellent reliability."*

**"How are transactions secure?"**
*"@Transactional ensures ACID compliance - either all operations succeed or all rollback. Plus business validations and comprehensive audit trails."*

**"What's @SpringBootApplication?"**
*"Combines @Configuration, @EnableAutoConfiguration, and @ComponentScan to bootstrap the entire application."*

**"Spring Boot vs Spring Framework?"**
*"Spring Boot eliminates boilerplate configuration and provides opinionated defaults for rapid development, while Spring Framework requires manual setup."*

## 🎯 **Confidence Boosters**
- ✅ **Modern Stack**: Java 21 LTS, Spring Boot 3.5.9
- ✅ **Banking Standards**: ACID transactions, audit trails
- ✅ **Security**: Refresh token rotation (enterprise pattern)
- ✅ **Production Ready**: Proper logging, exception handling
- ✅ **Scalable Design**: Stateless JWT, layered architecture

## 📊 **Technical Metrics**
- **Access Token**: 15 minutes (security best practice)
- **Refresh Token**: 7 days (balance of security/UX)
- **Connection Pool**: HikariCP (industry fastest)
- **Password**: BCrypt (adaptive, future-proof)

Remember: **You built something impressive - present it confidently!** 🚀
