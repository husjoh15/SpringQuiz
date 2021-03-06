package no.group3.SpringQuiz.user.model.repository

import no.group3.SpringQuiz.user.model.entity.User
import org.springframework.data.repository.CrudRepository
//import org.springframework.security.crypto.bcrypt.BCrypt
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext

@Repository
interface UserRepository : CrudRepository<User, Long>, UserRepositoryCustom {
    fun findByUserName(userName: String): User?
}


@Transactional
interface UserRepositoryCustom {
    // fun findByUserName(userName: String): User
    fun createUser(userName: String, firstName: String, lastName: String, email: String): Long
    fun deleteUserById(id: Long): Boolean
    fun updateUserName(id: Long, userName: String): Boolean
    fun updateFirstName(id: Long, firstName: String): Boolean
    fun updateLastName(id: Long, lastName: String): Boolean
    fun updateEmail(id: Long, email: String): Boolean
    fun updateUser(id: Long, userName: String, firstName: String, lastName: String, email: String): Boolean
    fun updateFirstNameLastNameAndEmail(id: Long, firstName: String, lastName: String, email: String): Boolean
}

class UserRepositoryImpl : UserRepositoryCustom {

    override fun deleteUserById(id: Long): Boolean {
        val user = em.find(User::class.java, id) ?: return false
        em.remove(user)
        return true
    }

    override fun createUser(userName: String, firstName: String, lastName: String, email: String): Long {
        //val hashedPassword = plainPassword//BCrypt.hashpw(plainPassword, BCrypt.gensalt(10))
        val user = User(null, userName, firstName, lastName, email)
        em.persist(user)
        return user.userId!!
    }

    @PersistenceContext
    private lateinit var em: EntityManager

    override fun updateFirstName(id: Long, firstName: String): Boolean {
        var user = em.find(User::class.java, id) ?: return false
        user.firstName = firstName
        return true
    }

    override fun updateLastName(id: Long, lastName: String): Boolean {
        var user = em.find(User::class.java, id) ?: return false
        user.lastName = lastName
        return true
    }

    override fun updateEmail(id: Long, email: String): Boolean {
        var user = em.find(User::class.java, id) ?: return false
        user.email = email
        return true
    }

    override fun updateUserName(id: Long, userName: String): Boolean {
        var user = em.find(User::class.java, id) ?: return false
        user.userName = userName
        return true
    }

    override fun updateFirstNameLastNameAndEmail(id: Long, firstName: String, lastName: String, email: String): Boolean {
        var user = em.find(User::class.java, id) ?: return false
        user.firstName= firstName
        user.lastName = lastName
        user.email = email
        return true
    }



    override fun updateUser(id: Long, userName: String, firstName: String, lastName: String, email: String): Boolean {
        var user = em.find(User::class.java, id) ?: return false
        user.userName = userName
        user.firstName = firstName
        user.lastName = lastName
        user.email = email
     //   user.passwordHash = plainPassword//BCrypt.hashpw(plainPassword, BCrypt.gensalt(10))
        return true
    }
}