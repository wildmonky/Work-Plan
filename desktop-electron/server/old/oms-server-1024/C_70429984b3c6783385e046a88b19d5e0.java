import com.zhuofan.core.datasource.JdbcDao;
import com.zhuofan.core.common.CommonFunc;
import com.zhuofan.core.common.CommonObject;
import java.util.HashMap;
import com.zhuofan.core.eval.Eval;
import com.zhuofan.core.orm.model.*;
import com.zhuofan.core.orm.dao.*;
public class C_70429984b3c6783385e046a88b19d5e0
{
    public Object getEvalResult(HashMap mapRow) throws Exception
    {
        String url="javascript:window.open('dataadd.html?dataname=sys_table_group&table_column_group_id&actiontype=edit&pk_id="+mapRow.get("TABLE_COLUMN_GROUP_ID")+"');";
return url;

    }
}
