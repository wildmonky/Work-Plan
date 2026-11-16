import com.zhuofan.core.datasource.JdbcDao;
import com.zhuofan.core.common.CommonFunc;
import com.zhuofan.core.common.CommonObject;
import java.util.HashMap;
import com.zhuofan.core.eval.Eval;
import com.zhuofan.core.orm.model.*;
import com.zhuofan.core.orm.dao.*;
public class C_75ff9da38d05858d37914197102e0849
{
    public Object getEvalResult(HashMap mapRow) throws Exception
    {
        String url="javascript:deleteTableData('sys_user','"+mapRow.get("USERID")+"');";
return url;

    }
}
