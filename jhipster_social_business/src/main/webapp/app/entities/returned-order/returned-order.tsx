import React, { useEffect, useState } from 'react';
import { Link, useLocation, useNavigate } from 'react-router-dom';
import { Button, Table } from 'reactstrap';
import { JhiItemCount, JhiPagination, TextFormat, Translate, getPaginationState } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faSort, faSortDown, faSortUp } from '@fortawesome/free-solid-svg-icons';
import { APP_DATE_FORMAT } from 'app/config/constants';
import { ASC, DESC, ITEMS_PER_PAGE, SORT } from 'app/shared/util/pagination.constants';
import { overridePaginationStateWithQueryParams } from 'app/shared/util/entity-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntities } from './returned-order.reducer';

export const ReturnedOrder = () => {
  const dispatch = useAppDispatch();

  const pageLocation = useLocation();
  const navigate = useNavigate();

  const [paginationState, setPaginationState] = useState(
    overridePaginationStateWithQueryParams(getPaginationState(pageLocation, ITEMS_PER_PAGE, 'id'), pageLocation.search),
  );

  const returnedOrderList = useAppSelector(state => state.returnedOrder.entities);
  const loading = useAppSelector(state => state.returnedOrder.loading);
  const totalItems = useAppSelector(state => state.returnedOrder.totalItems);

  const getAllEntities = () => {
    dispatch(
      getEntities({
        page: paginationState.activePage - 1,
        size: paginationState.itemsPerPage,
        sort: `${paginationState.sort},${paginationState.order}`,
      }),
    );
  };

  const sortEntities = () => {
    getAllEntities();
    const endURL = `?page=${paginationState.activePage}&sort=${paginationState.sort},${paginationState.order}`;
    if (pageLocation.search !== endURL) {
      navigate(`${pageLocation.pathname}${endURL}`);
    }
  };

  useEffect(() => {
    sortEntities();
  }, [paginationState.activePage, paginationState.order, paginationState.sort]);

  useEffect(() => {
    const params = new URLSearchParams(pageLocation.search);
    const page = params.get('page');
    const sort = params.get(SORT);
    if (page && sort) {
      const sortSplit = sort.split(',');
      setPaginationState({
        ...paginationState,
        activePage: +page,
        sort: sortSplit[0],
        order: sortSplit[1],
      });
    }
  }, [pageLocation.search]);

  const sort = p => () => {
    setPaginationState({
      ...paginationState,
      order: paginationState.order === ASC ? DESC : ASC,
      sort: p,
    });
  };

  const handlePagination = currentPage =>
    setPaginationState({
      ...paginationState,
      activePage: currentPage,
    });

  const handleSyncList = () => {
    sortEntities();
  };

  const getSortIconByFieldName = (fieldName: string) => {
    const sortFieldName = paginationState.sort;
    const order = paginationState.order;
    if (sortFieldName !== fieldName) {
      return faSort;
    }
    return order === ASC ? faSortUp : faSortDown;
  };

  return (
    <div>
      <h2 id="returned-order-heading" data-cy="ReturnedOrderHeading">
        <Translate contentKey="whatsappProductServiceProApp.returnedOrder.home.title">Returned Orders</Translate>
        <div className="d-flex justify-content-end">
          <Button className="me-2" color="info" onClick={handleSyncList} disabled={loading}>
            <FontAwesomeIcon icon="sync" spin={loading} />{' '}
            <Translate contentKey="whatsappProductServiceProApp.returnedOrder.home.refreshListLabel">Refresh List</Translate>
          </Button>
          <Link to="/returned-order/new" className="btn btn-primary jh-create-entity" id="jh-create-entity" data-cy="entityCreateButton">
            <FontAwesomeIcon icon="plus" />
            &nbsp;
            <Translate contentKey="whatsappProductServiceProApp.returnedOrder.home.createLabel">Create new Returned Order</Translate>
          </Link>
        </div>
      </h2>
      <div className="table-responsive">
        {returnedOrderList && returnedOrderList.length > 0 ? (
          <Table responsive>
            <thead>
              <tr>
                <th className="hand" onClick={sort('id')}>
                  <Translate contentKey="whatsappProductServiceProApp.returnedOrder.id">ID</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('id')} />
                </th>
                <th className="hand" onClick={sort('returnDate')}>
                  <Translate contentKey="whatsappProductServiceProApp.returnedOrder.returnDate">Return Date</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('returnDate')} />
                </th>
                <th className="hand" onClick={sort('paymentReceivedMode')}>
                  <Translate contentKey="whatsappProductServiceProApp.returnedOrder.paymentReceivedMode">Payment Received Mode</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('paymentReceivedMode')} />
                </th>
                <th className="hand" onClick={sort('paymentReturnedMode')}>
                  <Translate contentKey="whatsappProductServiceProApp.returnedOrder.paymentReturnedMode">Payment Returned Mode</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('paymentReturnedMode')} />
                </th>
                <th className="hand" onClick={sort('productClaimStatus')}>
                  <Translate contentKey="whatsappProductServiceProApp.returnedOrder.productClaimStatus">Product Claim Status</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('productClaimStatus')} />
                </th>
                <th className="hand" onClick={sort('refundAmount')}>
                  <Translate contentKey="whatsappProductServiceProApp.returnedOrder.refundAmount">Refund Amount</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('refundAmount')} />
                </th>
                <th>
                  <Translate contentKey="whatsappProductServiceProApp.returnedOrder.order">Order</Translate> <FontAwesomeIcon icon="sort" />
                </th>
                <th>
                  <Translate contentKey="whatsappProductServiceProApp.returnedOrder.customer">Customer</Translate>{' '}
                  <FontAwesomeIcon icon="sort" />
                </th>
                <th className="hand" onClick={sort('removedCustomerId')}>
                  <Translate contentKey="whatsappProductServiceProApp.returnedOrder.removedCustomerId">Removed Customer Id</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('removedCustomerId')} />
                </th>
                <th />
              </tr>
            </thead>
            <tbody>
              {returnedOrderList.map((returnedOrder, i) => (
                <tr key={`entity-${i}`} data-cy="entityTable">
                  <td>
                    <Button tag={Link} to={`/returned-order/${returnedOrder.id}`} color="link" size="sm">
                      {returnedOrder.id}
                    </Button>
                  </td>
                  <td>
                    {returnedOrder.returnDate ? <TextFormat type="date" value={returnedOrder.returnDate} format={APP_DATE_FORMAT} /> : null}
                  </td>
                  <td>{returnedOrder.paymentReceivedMode}</td>
                  <td>{returnedOrder.paymentReturnedMode}</td>
                  <td>
                    <Translate contentKey={`whatsappProductServiceProApp.ReturnProductStatus.${returnedOrder.productClaimStatus}`} />
                  </td>
                  <td>{returnedOrder.refundAmount}</td>
                  <td>
                    {returnedOrder.order ? <Link to={`/customer-order/${returnedOrder.order.id}`}>{returnedOrder.order.id}</Link> : ''}
                  </td>
                  <td>
                    {returnedOrder.customer ? <Link to={`/customer/${returnedOrder.customer.id}`}>{returnedOrder.customer.name}</Link> : ''}
                  </td>
                  <td>
                    {returnedOrder.removedCustomerId ? (
                      <Link to={`/removed-user/${returnedOrder.removedCustomerId}`}>{returnedOrder.removedCustomerId}</Link>
                    ) : (
                      ''
                    )}
                  </td>
                  <td className="text-end">
                    <div className="btn-group flex-btn-group-container">
                      <Button tag={Link} to={`/returned-order/${returnedOrder.id}`} color="info" size="sm" data-cy="entityDetailsButton">
                        <FontAwesomeIcon icon="eye" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.view">View</Translate>
                        </span>
                      </Button>
                      <Button
                        tag={Link}
                        to={`/returned-order/${returnedOrder.id}/edit?page=${paginationState.activePage}&sort=${paginationState.sort},${paginationState.order}`}
                        color="primary"
                        size="sm"
                        data-cy="entityEditButton"
                      >
                        <FontAwesomeIcon icon="pencil-alt" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.edit">Edit</Translate>
                        </span>
                      </Button>
                      <Button
                        onClick={() =>
                          (window.location.href = `/returned-order/${returnedOrder.id}/delete?page=${paginationState.activePage}&sort=${paginationState.sort},${paginationState.order}`)
                        }
                        color="danger"
                        size="sm"
                        data-cy="entityDeleteButton"
                      >
                        <FontAwesomeIcon icon="trash" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.delete">Delete</Translate>
                        </span>
                      </Button>
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </Table>
        ) : (
          !loading && (
            <div className="alert alert-warning">
              <Translate contentKey="whatsappProductServiceProApp.returnedOrder.home.notFound">No Returned Orders found</Translate>
            </div>
          )
        )}
      </div>
      {totalItems ? (
        <div className={returnedOrderList && returnedOrderList.length > 0 ? '' : 'd-none'}>
          <div className="justify-content-center d-flex">
            <JhiItemCount page={paginationState.activePage} total={totalItems} itemsPerPage={paginationState.itemsPerPage} i18nEnabled />
          </div>
          <div className="justify-content-center d-flex">
            <JhiPagination
              activePage={paginationState.activePage}
              onSelect={handlePagination}
              maxButtons={5}
              itemsPerPage={paginationState.itemsPerPage}
              totalItems={totalItems}
            />
          </div>
        </div>
      ) : (
        ''
      )}
    </div>
  );
};

export default ReturnedOrder;
