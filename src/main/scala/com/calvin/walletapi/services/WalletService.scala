package com.calvin.walletapi.services

import akka.NotUsed
import akka.stream.scaladsl.Source
import com.calvin.walletapi.actors.Wallet._
import com.calvin.walletapi.domain.WalletId
import com.calvin.walletapi.services.Error._
import com.calvin.walletapi.services.Response._
import zio.Task

sealed trait Error
object Error {
  type WalletNotCreated   = WalletNotCreated.type
  type AlreadyCreated     = AlreadyCreated.type
  type InsufficientAmount = InsufficientAmount.type

  case object AlreadyCreated      extends Error
  case object WalletNotCreated    extends Error
  case object InsufficientAmount  extends Error
  case object WithdrawOverBalance extends Error
}

sealed trait Response
object Response {
  type SuccessfulCreation = SuccessfulCreation.type

  case object SuccessfulCreation                                           extends Response
  case class Balance(amount: Long)                                         extends Response
  case class SuccessfulDeposit(amount: Long, feeAppliedToBalance: Long)    extends Response
  case class SuccessfulWithdrawal(amount: Long, feeAppliedToBalance: Long) extends Response
  case class History(events: List[Event])                                  extends Response
  case class FeeReport(percentage: Double, fee: Long)                      extends Response
}

trait WalletService {
  def create(walletId: WalletId): Task[Either[AlreadyCreated, SuccessfulCreation]]

  def queryDepositFee(walletId: WalletId, amount: Long): Task[Either[WalletNotCreated, FeeReport]]

  def queryWithdrawFee(walletId: WalletId, amount: Long): Task[Either[WalletNotCreated, FeeReport]]

  def deposit(
    walletId: WalletId,
    amount: Long
  ): Task[Either[Error, SuccessfulDeposit]]

  def withdraw(
    walletId: WalletId,
    amount: Long
  ): Task[Either[Error, SuccessfulWithdrawal]]

  def balance(walletId: WalletId): Task[Either[WalletNotCreated, Balance]]

  def immediateHistory(walletId: WalletId): Task[Either[WalletNotCreated, History]]

  def allHistory(walletId: WalletId): Task[Either[WalletNotCreated, Source[Event, NotUsed]]]
}
