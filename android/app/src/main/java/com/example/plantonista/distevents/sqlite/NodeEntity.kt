package com.example.plantonista.distevents.sqlite

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import androidx.room.Relation
import com.example.plantonista.distevents.NodeData

@Entity("node")
data class NodeEntity(
    @PrimaryKey
    val email: String,
    val publicIP: String,
    val updatedAt: Long,
)

@Entity(
    tableName = "node_local_ip",
    primaryKeys = ["email", "localIP"],
    foreignKeys = [
        ForeignKey(entity = NodeEntity::class, parentColumns = ["email"], childColumns = ["email"], onDelete = ForeignKey.CASCADE)
    ]
)
data class NodeLocalIP(
    val email: String,
    val localIP: String
)


data class NodeWithLocalIPs(
    @Embedded val node: NodeEntity,
    @Relation(
        parentColumn = "email",
        entityColumn = "email"
    ) val localIPs: List<NodeLocalIP>
) {
    fun toData(): NodeData = NodeData(
        node.email,
        node.publicIP,
        node.updatedAt,
        localIPs.map { it.localIP }
    )
}

fun NodeData.toEntity() = NodeWithLocalIPs(
    NodeEntity(email, publicIP, updatedAt),
    (localIPs ?: listOf()).map { NodeLocalIP(email, it) }
)
