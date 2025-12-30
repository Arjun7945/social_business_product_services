import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row, UncontrolledTooltip } from 'reactstrap';
import { TextFormat, Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './customer-order.reducer';

export const CustomerOrderDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const customerOrderEntity = useAppSelector(state => state.customerOrder.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="customerOrderDetailsHeading">
          <Translate contentKey="whatsappProductServiceProApp.customerOrder.detail.title">CustomerOrder</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{customerOrderEntity.id}</dd>
          <dt>
            <span id="orderTime">
              <Translate contentKey="whatsappProductServiceProApp.customerOrder.orderTime">Order Time</Translate>
            </span>
          </dt>
          <dd>
            {customerOrderEntity.orderTime ? (
              <TextFormat value={customerOrderEntity.orderTime} type="date" format={APP_DATE_FORMAT} />
            ) : null}
          </dd>
          <dt>
            <span id="totalAmount">
              <Translate contentKey="whatsappProductServiceProApp.customerOrder.totalAmount">Total Amount</Translate>
            </span>
          </dt>
          <dd>{customerOrderEntity.totalAmount}</dd>
          <dt>
            <span id="status">
              <Translate contentKey="whatsappProductServiceProApp.customerOrder.status">Status</Translate>
            </span>
            <UncontrolledTooltip target="status">
              <Translate contentKey="whatsappProductServiceProApp.customerOrder.help.status" />
            </UncontrolledTooltip>
          </dt>
          <dd>{customerOrderEntity.status}</dd>
          <dt>
            <span id="paymentMethod">
              <Translate contentKey="whatsappProductServiceProApp.customerOrder.paymentMethod">Payment Method</Translate>
            </span>
          </dt>
          <dd>{customerOrderEntity.paymentMethod}</dd>
          <dt>
            <span id="confirmedAt">
              <Translate contentKey="whatsappProductServiceProApp.customerOrder.confirmedAt">Confirmed At</Translate>
            </span>
          </dt>
          <dd>
            {customerOrderEntity.confirmedAt ? (
              <TextFormat value={customerOrderEntity.confirmedAt} type="date" format={APP_DATE_FORMAT} />
            ) : null}
          </dd>
          <dt>
            <span id="removedCustomerId">
              <Translate contentKey="whatsappProductServiceProApp.customerOrder.removedCustomerId">Removed Customer Id</Translate>
            </span>
          </dt>
          <dd>
            {customerOrderEntity.removedCustomerId ? (
              <Link to={`/removed-user/${customerOrderEntity.removedCustomerId}`}>{customerOrderEntity.removedCustomerId}</Link>
            ) : (
              ''
            )}
          </dd>
          <dt>
            <span id="removedDeliveryPersonId">
              <Translate contentKey="whatsappProductServiceProApp.customerOrder.removedDeliveryPersonId">
                Removed Delivery Person Id
              </Translate>
            </span>
          </dt>
          <dd>
            {customerOrderEntity.removedDeliveryPersonId ? (
              <Link to={`/removed-user/${customerOrderEntity.removedDeliveryPersonId}`}>{customerOrderEntity.removedDeliveryPersonId}</Link>
            ) : (
              ''
            )}
          </dd>
          <dt>
            <span id="transactionId">
              <Translate contentKey="whatsappProductServiceProApp.customerOrder.transactionId">Transaction Id</Translate>
            </span>
          </dt>
          <dd>{customerOrderEntity.transactionId}</dd>
          <dt>
            <Translate contentKey="whatsappProductServiceProApp.customerOrder.history">History</Translate>
          </dt>
          <dd>
            {customerOrderEntity.history ? (
              <Link to={`/order-status-history/${customerOrderEntity.history.id}`}>{customerOrderEntity.history.id}</Link>
            ) : (
              ''
            )}
          </dd>
          <dt>
            <Translate contentKey="whatsappProductServiceProApp.customerOrder.customer">Customer</Translate>
          </dt>
          <dd>
            {customerOrderEntity.customer ? (
              <Link to={`/customer/${customerOrderEntity.customer.id}`}>{customerOrderEntity.customer.name}</Link>
            ) : (
              ''
            )}
          </dd>
          <dt>
            <Translate contentKey="whatsappProductServiceProApp.customerOrder.deliveryPerson">Delivery Person</Translate>
          </dt>
          <dd>
            {customerOrderEntity.deliveryPerson ? (
              <Link to={`/delivery-person/${customerOrderEntity.deliveryPerson.id}`}>{customerOrderEntity.deliveryPerson.name}</Link>
            ) : (
              ''
            )}
          </dd>
        </dl>
        <Button tag={Link} to="/customer-order" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/customer-order/${customerOrderEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default CustomerOrderDetail;
